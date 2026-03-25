package dealer.task3.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Servlet filter that:
 *  1. Reads X-Tenant-Id from the request header.
 *  2. Returns HTTP 400 if the header is missing or blank
 *     (except for the /admin/** path which requires GLOBAL_ADMIN role
 *      but still needs the header).
 *  3. Stores tenant + role in TenantContext for the duration of the request.
 *  4. Clears TenantContext after the request completes.
 *
 * X-Role header is optional; defaults to "USER".
 * Accepted role values: USER, GLOBAL_ADMIN
 */
@Component
@Order(1)
public class TenantFilter implements Filter {

    private static final String TENANT_HEADER = "X-Tenant-Id";
    private static final String ROLE_HEADER   = "X-Role";

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest  httpReq  = (HttpServletRequest)  request;
        HttpServletResponse httpResp = (HttpServletResponse) response;

        String tenantId = httpReq.getHeader(TENANT_HEADER);
        String role     = httpReq.getHeader(ROLE_HEADER);

        if (tenantId == null || tenantId.isBlank()) {
            httpResp.setContentType("application/json");
            httpResp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResp.getWriter().write(
                "{\"error\":\"Missing required header: X-Tenant-Id\"}"
            );
            return;
        }

        TenantContext.setTenantId(tenantId.trim());
        TenantContext.setRole(role != null ? role.trim().toUpperCase() : "USER");

        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
