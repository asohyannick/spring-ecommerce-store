package com.mercado.mercadoSpring.service.auth;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.mercado.mercadoSpring.config.JwtUtil;
import com.mercado.mercadoSpring.constants.user.UserRole;
import com.mercado.mercadoSpring.dto.auth.LoginDto;
import com.mercado.mercadoSpring.dto.auth.OTPRequest;
import com.mercado.mercadoSpring.dto.auth.RegistrationDto;
import com.mercado.mercadoSpring.dto.auth.ResponseDto;
import com.mercado.mercadoSpring.entity.auth.Auth;
import com.mercado.mercadoSpring.exception.AccountBlockedException;
import com.mercado.mercadoSpring.mappers.auth.AuthMapper;
import com.mercado.mercadoSpring.repository.auth.AuthRepository;
import com.mercado.mercadoSpring.utils.RoleAssigner;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
@Service
@Transactional
public class AuthService {
  private final AuthRepository authRepository;
  private final AuthMapper authMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final JavaMailSender mailSender;

    @Value("${FIREBASE_PRIVATE_KEY}")
    private String firebasePrivateKey;

    @Value("${FIREBASE_CLIENT_EMAIL}")
    private String firebaseClientEmail;

    @Value("${FIREBASE_PROJECT_ID}")
    private String firebaseProjectId;

    public AuthService(
            AuthRepository authRepository,
            AuthMapper authMapper,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            JavaMailSender mailSender
            ) {
        this.authRepository = authRepository;
        this.authMapper = authMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.mailSender = mailSender;
    }

    private String generate2FACode(Auth savedAuth) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        savedAuth.setTwoFactorExpiry(LocalDateTime.now().plusMinutes(5)); // 5 minutes expiry
        savedAuth.setTwoFactorAttempts(0);
        return otp;
    }

    private void send2FACodeEmail(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Mercado Account Verification: Your 2FA Security Code");

            String htmlContent = """
        <div style="margin:0;padding:0;background-color:#f5f7fb;font-family:Arial,Helvetica,sans-serif;">
          <div style="max-width:640px;margin:30px auto;background:#ffffff;
                      border:1px solid #e5e7eb;border-radius:12px;
                      box-shadow:0 6px 18px rgba(17,24,39,0.06);overflow:hidden;">

            <!-- Header -->
            <div style="padding:22px 24px;
                        background:linear-gradient(135deg,#0f172a,#1d4ed8);
                        color:#ffffff;">
              <h2 style="margin:0;font-size:18px;">Mercado Security</h2>
              <p style="margin:6px 0 0;font-size:13px;opacity:0.9;">
                Two-Factor Authentication (2FA)
              </p>
            </div>

            <!-- Content -->
            <div style="padding:24px;color:#1f2937;font-size:14px;line-height:1.6;">
              <p>Dear Mercado User,</p>

              <p>Thank you for securing your account.</p>

              <p>
                To verify your identity and complete your login or registration,
                please use the following Two-Factor Authentication (2FA) code:
              </p>

              <!-- Code Box -->
              <div style="margin:20px 0;padding:18px;
                          background:#eff6ff;border:1px solid #bfdbfe;
                          border-radius:12px;text-align:center;">
                <p style="margin:0 0 10px;font-size:12px;
                          text-transform:uppercase;letter-spacing:2px;
                          font-weight:bold;color:#1e40af;">
                  Your Verification Code
                </p>
                <div style="display:inline-block;
                            padding:10px 16px;
                            font-size:28px;
                            font-weight:800;
                            letter-spacing:6px;
                            font-family:'Courier New',Courier,monospace;
                            background:#ffffff;
                            border:1px solid #bfdbfe;
                            border-radius:10px;
                            color:#0b1220;">
                  %s
                </div>
              </div>

              <!-- Security Notice -->
              <div style="margin-top:20px;padding:14px;
                          background:#fffbeb;
                          border-left:4px solid #f59e0b;
                          border-radius:10px;">
                <p style="margin:0 0 8px;font-weight:bold;color:#92400e;">
                  ⚠️ Important Security Notice
                </p>
                <ol style="margin:0;padding-left:18px;color:#78350f;">
                  <li>This code is valid for the next <strong>5 minutes</strong>.</li>
                  <li><strong>Do not share this code</strong> with anyone, including Mercado employees.</li>
                </ol>
              </div>

              <p style="margin-top:20px;color:#6b7280;font-size:13px;">
                If you did not attempt to access or register a Mercado account,
                please disregard this email. Your account security remains intact.
              </p>

              <p style="margin-top:18px;">
                If you have any questions, please contact our support team.
              </p>

              <p style="margin-top:20px;">
                Sincerely,<br/>
                <strong>The Mercado Team</strong> led by Asoh Yannick
              </p>
            </div>

            <!-- Footer -->
            <div style="padding:16px 24px;
                        background:#f9fafb;
                        border-top:1px solid #e5e7eb;
                        font-size:12px;
                        color:#6b7280;">
              This is an automated security message. Please do not reply.
            </div>

          </div>
        </div>
        """.formatted(code);

            helper.setText(htmlContent, true); // true = HTML
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send 2FA email", e);
        }
    }

    public ResponseDto register(RegistrationDto registrationDto) {
        if (authRepository.existsByEmail(registrationDto.email())) {
         throw new RuntimeException("Email already exists");
        }
        Auth auth = authMapper.toAuthEntity(registrationDto);
        auth.setPassword(passwordEncoder.encode(registrationDto.password()));

        // Assign role based on email
        auth.setRole(RoleAssigner.assignRole(registrationDto.email()));

        // Generate 6-digit 2FA code
        String twoFactorCode = generate2FACode(auth);
        auth.setTwoFactorSecret(twoFactorCode);
        auth.setIsTwoFactorVerified(false);
        auth.setIsTwoFactorVerified(Boolean.valueOf(String.valueOf(false)));

        // Generate access + refresh tokens
        String accessToken = jwtUtil.generateAccessToken(auth.getEmail().trim().toLowerCase(), String.valueOf(auth.getRole().name()));
        String refreshToken = jwtUtil.generateRefreshToken(auth.getEmail().trim().toLowerCase());

        auth.setAccessToken(accessToken);
        auth.setRefreshToken(refreshToken);

        Auth savedAuth = authRepository.save(auth);
        // Send 2FA code via email
        send2FACodeEmail(savedAuth.getEmail(), twoFactorCode);

        // Return safe ResponseDTO (without password)
        return new ResponseDto(
                savedAuth.getFirstName(),
                savedAuth.getLastName(),
                savedAuth.getEmail(),
                savedAuth.getIsAccountBlocked(),
                UserRole.valueOf(String.valueOf(savedAuth.getRole()))
        );
    }

    public Auth verifyOTP(OTPRequest otpRequest) {
        // The DTO validation will have already ensured this is not blank and 6 chars
        String otp = otpRequest.getOtp().trim();

        // Find the user who has this OTP and is not yet verified
        Auth auth = authRepository.findByTwoFactorSecretAndIsTwoFactorVerifiedFalse(otp)
                .orElseThrow(() -> new RuntimeException("Invalid or expired OTP"));

        if (auth.getTwoFactorExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired. Request a new one.");
        }

        // Mark 2FA as verified and clear OTP
        auth.setIsTwoFactorVerified(true);
        auth.setMagicTokenExpiration(LocalDateTime.now().plusMinutes(15));
        return authRepository.save(auth);
    }

    public Auth resendOTP(Map<String, String> request) {
        String email = request.get("email").trim().toLowerCase();
        if (email.isBlank()) {
            throw new RuntimeException("Email must be provided");
        }

        Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(Boolean.TRUE.equals(auth.getIsTwoFactorVerified())) {
            throw new RuntimeException("2FA already verified");
        }

        if(auth.getTwoFactorAttempts() >= 5) {
            throw new RuntimeException("Maximum OTP attempts reached. Please try again later.");
        }

        if(
                auth.getTwoFactorExpiry() != null && auth.getTwoFactorExpiry().isAfter(LocalDateTime.now()) && auth.getTwoFactorSecret() != null
        ) {
            throw new RuntimeException("OTP already sent. Please check your email.");
        }

        // Generate new OTP
        String newOtp = generate2FACode(auth);
        auth.setTwoFactorSecret(newOtp);
        authRepository.save(auth);

        // Send OTP email
        send2FACodeEmail(auth.getEmail().trim().toLowerCase(), newOtp);

        return auth;
    }

    public ResponseDto refreshToken(String refreshToken) {
        Auth auth = authRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));
        try {
            jwtUtil.validateToken(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Refresh token has expired, please login again");
        }

        // Generate new tokens
        String newAccessToken = jwtUtil.generateAccessToken(auth.getEmail().trim().toLowerCase(), String.valueOf(auth.getRole()));
        String newRefreshToken = jwtUtil.generateRefreshToken(auth.getEmail().trim().toLowerCase());

        auth.setAccessToken(newAccessToken);
        auth.setRefreshToken(newRefreshToken);

        authRepository.save(auth);

        return new ResponseDto(
                auth.getFirstName(),
                auth.getLastName(),
                auth.getEmail(),
                auth.getIsAccountBlocked(),
                UserRole.valueOf(String.valueOf(auth.getRole()))
        );
    }

    public void sendMagicLink(String email) {
        String sanitizedEmail = email.trim().toLowerCase();

        Auth auth = authRepository.findByEmail(sanitizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtUtil.generateAccessToken(auth.getEmail().trim().toLowerCase(), String.valueOf(auth.getRole()));
        auth.setMagicToken(token);
        auth.setMagicTokenExpiration(LocalDateTime.now().plusMinutes(15)); // 15 minutes expiry
        authRepository.save(auth);
        String magicLink = "http://localhost:3000/magic-login?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Magic Login Link");
        message.setText("Click the following link to log in: " + magicLink +
                "\n\nThis link will expire in 15 minutes.");
        mailSender.send(message);
    }

    public ResponseDto loginWithMagicLink(String token) {
        Auth auth = authRepository.findByMagicToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        if (auth.getMagicTokenExpiration().isBefore(LocalDateTime.now()) || auth.getMagicTokenExpiration() == null) {
            throw new RuntimeException("Token has expired");
        }
        // Invalidate the token after use
        auth.setMagicToken(null);
        auth.setMagicTokenExpiration(null);
        String accessToken = jwtUtil.generateAccessToken(auth.getEmail().trim().toLowerCase(), String.valueOf(auth.getRole()));
        String refreshToken = jwtUtil.generateRefreshToken(auth.getEmail().trim().toLowerCase());
        auth.setAccessToken(accessToken);
        auth.setRefreshToken(refreshToken);
        authRepository.save(auth);
        return new ResponseDto(
                auth.getFirstName(),
                auth.getLastName(),
                auth.getEmail(),
                auth.getIsAccountBlocked(),
                UserRole.valueOf(String.valueOf(auth.getRole()))
        );
    }

    public Auth login(LoginDto loginDto) {
        Auth auth = authRepository.findByEmail(loginDto.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        if (auth.getIsAccountBlocked()) {
            throw new AccountBlockedException("Your account has been blocked. Contact the System Administrator.");
        }

        if (!passwordEncoder.matches(loginDto.password(), auth.getPassword())) {
            auth.setFailedLoginAttempts(auth.getFailedLoginAttempts() + 1);

            // Block logic
            if (auth.getFailedLoginAttempts() >= 5) {
                auth.setIsAccountBlocked(true);
                authRepository.save(auth);
                throw new AccountBlockedException("Your account has been blocked due to multiple failed login attempts. Contact the System Administrator.");
            }

            authRepository.save(auth);
            throw new BadCredentialsException("Invalid email or password");
        }

        // 4. Success Path
        auth.setIsEmailVerified(true);
        auth.setFailedLoginAttempts(0);

        String accessToken = jwtUtil.generateAccessToken(auth.getEmail().trim().toLowerCase(), String.valueOf(auth.getRole().name()));
        String refreshToken = jwtUtil.generateRefreshToken(auth.getEmail().trim().toLowerCase());
        auth.setAccessToken(accessToken);
        auth.setRefreshToken(refreshToken);

        authRepository.save(auth);
        return auth;
    }

    public ResponseDto logout(Long userId) {
      Auth auth = authRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        auth.setAccessToken(null);
        auth.setRefreshToken(null);
        authRepository.save(auth);
        return new ResponseDto(
                auth.getFirstName(),
                auth.getLastName(),
                auth.getEmail(),
                auth.getIsAccountBlocked(),
                UserRole.valueOf(String.valueOf(auth.getRole()))
        );
    }

    public ResponseDto deleteAccount(Long userId) {
        Auth auth = authRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        authRepository.delete(auth);
        return new ResponseDto(
                auth.getFirstName(),
                auth.getLastName(),
                auth.getEmail(),
                auth.getIsAccountBlocked(),
                UserRole.valueOf(String.valueOf(auth.getRole()))
        );
    }

    public String forgotPassword(String email) {
        Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate six digit 2FA code
        String resetCode = generate2FACode(auth);

        auth.setTwoFactorSecret(resetCode);
        auth.setTwoFactorExpiry(LocalDateTime.now().plusMinutes(15)); // 15 minutes expiry (fix)
        auth.setIsTwoFactorVerified(false);
        authRepository.save(auth);
        String html = """
        <!DOCTYPE html>
        <html>
        <head>
          <meta charset="UTF-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0" />
          <title>Password Reset</title>
        </head>
        <body style="margin:0;padding:0;background:#f6f9fc;font-family:Arial,Helvetica,sans-serif;">
          <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:#f6f9fc;padding:24px 0;">
            <tr>
              <td align="center">
                <table role="presentation" width="600" cellspacing="0" cellpadding="0"
                       style="width:600px;max-width:92%%;background:#ffffff;border-radius:12px;overflow:hidden;
                              box-shadow:0 6px 18px rgba(0,0,0,0.08);">
                  <tr>
                    <td style="background:#0b5ed7;padding:18px 24px;color:#ffffff;">
                      <h2 style="margin:0;font-size:18px;line-height:1.4;">Password Reset Verification</h2>
                    </td>
                  </tr>

                  <tr>
                    <td style="padding:24px;color:#1f2937;">
                      <p style="margin:0 0 14px;font-size:14px;line-height:1.6;">
                        Hi there, 👋
                      </p>

                      <p style="margin:0 0 16px;font-size:14px;line-height:1.6;">
                        We received a request to reset your password. Use the verification code below to continue:
                      </p>

                      <div style="text-align:center;margin:22px 0;">
                        <span style="display:inline-block;background:#f3f4f6;border:1px solid #e5e7eb;
                                     padding:14px 20px;border-radius:10px;font-size:28px;letter-spacing:6px;
                                     font-weight:700;color:#111827;">
                          %s
                        </span>
                      </div>

                      <p style="margin:0 0 10px;font-size:13px;line-height:1.6;color:#374151;">
                        ⏳ This code will expire in <strong>15 minutes</strong>.
                      </p>

                      <p style="margin:0;font-size:13px;line-height:1.6;color:#6b7280;">
                        If you didn’t request this, you can safely ignore this email.
                      </p>
                    </td>
                  </tr>

                  <tr>
                    <td style="padding:16px 24px;background:#f9fafb;border-top:1px solid #e5e7eb;color:#6b7280;">
                      <p style="margin:0;font-size:12px;line-height:1.5;">
                        © %d Your App. All rights reserved.
                      </p>
                    </td>
                  </tr>

                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """.formatted(resetCode, java.time.Year.now().getValue());
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Password Reset Verification Code");
            helper.setText(html, true); // true = HTML

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
        return resetCode;
    }

    public void resetPassword(String newPassword, String resetTokenFromCookie) {

        // Check if cookie exists
        if (resetTokenFromCookie == null || resetTokenFromCookie.isBlank()) {
            throw new RuntimeException("Reset token missing");
        }

        // Find user by reset token stored in database
        Auth auth = authRepository.findByTwoFactorSecret(resetTokenFromCookie)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        // Check if token expired
        if (auth.getTwoFactorExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token has expired");
        }

        // Update password
        auth.setPassword(passwordEncoder.encode(newPassword));
        auth.setTwoFactorSecret(null);      // Clear token
        auth.setTwoFactorExpiry(null);      // Clear token expiry

        authRepository.save(auth);
    }

    public List<Auth> findAllUsers() {
        return authRepository.findAll();
    }
    public Auth findUserById(Long userId) {
        return authRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Auth blockUser(Long userId) {
        Auth auth = authRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (Boolean.TRUE.equals(auth.getIsAccountBlocked())) {
            throw new RuntimeException("User account is already blocked");
        }
        auth.setIsAccountBlocked(true);
        return authRepository.save(auth);
    }

    public Auth unBlockUser(Long userId) {
        Auth auth = authRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (!Boolean.TRUE.equals(auth.getIsAccountBlocked())) {
            throw new RuntimeException("User account is not blocked");
        }
        auth.setIsAccountBlocked(false);
        return authRepository.save(auth);
    }

    // Initialize Firebase with environment variables
    @PostConstruct
    public void init() throws Exception {
        String privateKey = firebasePrivateKey.replace("\\n", "\n"); // replace escaped newlines
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(
                        com.google.auth.oauth2.GoogleCredentials.fromStream(
                                new ByteArrayInputStream((
                                        "{\n" +
                                                "  \"type\": \"service_account\",\n" +
                                                "  \"project_id\": \"" + firebaseProjectId + "\",\n" +
                                                "  \"private_key_id\": \"ignored\",\n" +
                                                "  \"private_key\": \"" + privateKey + "\",\n" +
                                                "  \"client_email\": \"" + firebaseClientEmail + "\",\n" +
                                                "  \"client_id\": \"ignored\",\n" +
                                                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                                                "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                                                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                                                "  \"client_x509_cert_url\": \"ignored\"\n" +
                                                "}").getBytes(StandardCharsets.UTF_8))
                        )
                ).build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }

    public Auth loginWithGoogle(String googleToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(googleToken);
        if (decodedToken == null || decodedToken.getEmail() == null || decodedToken.getEmail().isBlank()) {
            throw new RuntimeException("Invalid Firebase token");
        }
        String email = decodedToken.getEmail().trim().toLowerCase();
        Auth auth = authRepository.findByEmail(email)
                .orElseGet(() -> createUserFromFirebase(decodedToken));
        String accessToken = jwtUtil.generateAccessToken(auth.getEmail().trim().toLowerCase(), String.valueOf(auth.getRole()));
        String refreshToken = jwtUtil.generateRefreshToken(auth.getEmail().trim().toLowerCase());
        auth.setAccessToken(accessToken);
        auth.setRefreshToken(refreshToken);
        return authRepository.save(auth);
    }

    private Auth createUserFromFirebase(FirebaseToken token) {
        Auth newUser = new Auth();
        newUser.setEmail(token.getEmail().trim().toLowerCase());
        newUser.setFirstName(token.getName() != null ? token.getName().trim().split(" ")[0] : "CUSTOMER");
        newUser.setLastName(token.getName() != null && token.getName().trim().contains(" ") ? token.getName().split(" ")[1] : "CUSTOMER");
        newUser.setIsEmailVerified(token.isEmailVerified());
        newUser.setRole(UserRole.valueOf("CUSTOMER"));
        newUser.setIsAccountBlocked(false);
        return authRepository.save(newUser);
    }
}
