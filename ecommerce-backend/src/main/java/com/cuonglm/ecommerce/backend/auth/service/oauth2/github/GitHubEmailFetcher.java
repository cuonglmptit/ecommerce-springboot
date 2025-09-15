package com.cuonglm.ecommerce.backend.auth.service.oauth2.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * GitHubEmailFetcher – Service chuyên biệt dùng để gọi GitHub API /user/emails.
 *
 * <p>
 * Mục đích: lấy địa chỉ email chính (primary) và đã xác minh (verified) khi nó bị ẩn.
 * </p>
 *
 * @author cuonglmptit
 * @since Wednesday, 29 October 2025
 */
@Service
public class GitHubEmailFetcher {

    private static final String BEARER_PREFIX = "Bearer ";
    private final String emailsUrl;

    private final RestClient restClient;

    // RestClient được Inject thông qua Constructor (cần RestClientConfig)
    public GitHubEmailFetcher(RestClient restClient
            , @Value("${third-party.github.emails-url}") String emailsUrl) {
        this.restClient = restClient;
        this.emailsUrl = emailsUrl;
    }

    /**
     * Gọi GitHub API để lấy danh sách email và lọc ra email chính và đã xác minh.
     *
     * @param token Access Token của người dùng GitHub.
     * @return Địa chỉ email chính đã được xác minh, hoặc null nếu không tìm thấy.
     */
    public String fetchPrimaryVerifiedEmailAddress(String token) {
        try {
            List<GitHubEmailVm> emails = restClient.get()
                    .uri(emailsUrl)
                    .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            (req, res) -> System.err.println("GitHub Email API error: " + res.getStatusCode()))
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (emails == null || emails.isEmpty()) return null;

            // Lọc và trả về email chính và đã xác minh
            return emails.stream()
                    .filter(vm -> Boolean.TRUE.equals(vm.primary()) && Boolean.TRUE.equals(vm.verified()))
                    .map(GitHubEmailVm::email)
                    .findFirst()
                    // Nếu không có thì trả về null
                    .orElse(null);

        } catch (Exception e) {
            System.err.println("Lỗi khi gọi GitHub Email API: " + e.getMessage());
            return null;
        }
    }

    /**
     * Record mô hình dữ liệu (Vm = View Model) email trả về từ GitHub.
     * (Thường là: email, primary, verified, visibility)
     */
    private record GitHubEmailVm(String email, Boolean primary, Boolean verified) {
    }
}