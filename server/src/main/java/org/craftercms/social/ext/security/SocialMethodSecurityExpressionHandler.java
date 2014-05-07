package org.craftercms.social.ext.security;

import org.aopalliance.intercept.MethodInvocation;
import org.craftercms.social.services.PermissionService;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * Created by cortiz on 4/16/14.
 */
public class SocialMethodSecurityExpressionHandler  extends DefaultMethodSecurityExpressionHandler {

    private PermissionService permissionService;

    @Override
    public StandardEvaluationContext createEvaluationContextInternal(final Authentication auth,
                                                                     final MethodInvocation mi) {
        StandardEvaluationContext ctx = super.createEvaluationContextInternal(auth, mi);
        ctx.setRootObject(new SocialSecurityExpressionRoot(auth,permissionService));
        return ctx;
    }

    public void setPermissionService(final PermissionService permissionService) {
        this.permissionService = permissionService;
    }
}
