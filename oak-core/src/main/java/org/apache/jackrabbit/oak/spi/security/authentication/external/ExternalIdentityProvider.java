begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*************************************************************************  *  * ADOBE CONFIDENTIAL  * ___________________  *  *  Copyright ${today.year} Adobe Systems Incorporated  *  All Rights Reserved.  *  * NOTICE:  All information contained herein is, and remains  * the property of Adobe Systems Incorporated and its suppliers,  * if any.  The intellectual and technical concepts contained  * herein are proprietary to Adobe Systems Incorporated and its  * suppliers and are protected by trade secret or copyright law.  * Dissemination of this information or reproduction of this material  * is strictly forbidden unless prior written permission is obtained  * from Adobe Systems Incorporated.  **************************************************************************/
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|security
operator|.
name|authentication
operator|.
name|external
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Credentials
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginException
import|;
end_import

begin_comment
comment|/**  * {@code ExternalIdentityProvider} defines an interface to an external system that provides users and groups that  * can be synced with local ones.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ExternalIdentityProvider
block|{
comment|/**      * Returns the name of this provider.      * @return the provider name.      */
annotation|@
name|Nonnull
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Returns the identity for the given reference or {@code null} if it does not exist. The provider should check if      * the {@link ExternalIdentityRef#getProviderName() provider name} matches his own name or is {@code null} and      * should not return a foreign identity.      *      * @param ref the reference      * @return an identity or {@code null}      *      * @throws ExternalIdentityException if an error occurs.      */
annotation|@
name|CheckForNull
name|ExternalIdentity
name|getIdentity
parameter_list|(
annotation|@
name|Nonnull
name|ExternalIdentityRef
name|ref
parameter_list|)
throws|throws
name|ExternalIdentityException
function_decl|;
comment|/**      * Returns the user for the given (local) id. if the user does not exist {@code null} is returned.      * @param userId the user id.      * @return the user or {@code null}      *      * @throws ExternalIdentityException if an error occurs.      */
annotation|@
name|CheckForNull
name|ExternalUser
name|getUser
parameter_list|(
annotation|@
name|Nonnull
name|String
name|userId
parameter_list|)
throws|throws
name|ExternalIdentityException
function_decl|;
comment|/**      * Authenticates the user represented by the given credentials and returns it. If the user does not exist in this      * provider, {@code null} is returned. If the authentication fails, a LoginException is thrown.      *      * @param credentials the credentials      * @return the user or {@code null}      * @throws ExternalIdentityException if an error occurs      * @throws javax.security.auth.login.LoginException if the user could not be authenticated      */
annotation|@
name|CheckForNull
name|ExternalUser
name|authenticate
parameter_list|(
annotation|@
name|Nonnull
name|Credentials
name|credentials
parameter_list|)
throws|throws
name|ExternalIdentityException
throws|,
name|LoginException
function_decl|;
comment|/**      * Returns the group for the given (local) group name. if the group does not exist {@code null} is returned.      * @param name the group name      * @return the group or {@code null}      *      * @throws ExternalIdentityException if an error occurs.      */
annotation|@
name|CheckForNull
name|ExternalGroup
name|getGroup
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
throws|throws
name|ExternalIdentityException
function_decl|;
block|}
end_interface

end_unit

