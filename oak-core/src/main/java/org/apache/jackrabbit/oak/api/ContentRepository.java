begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|api
package|;
end_package

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
name|jcr
operator|.
name|NoSuchWorkspaceException
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
comment|/**  * Oak content repository. The repository may be local or remote, or a cluster  * of any size. These deployment details are all hidden behind this interface.  *<p>  * All access to the repository happens through authenticated  * {@link ContentSession} instances acquired through the  * {@link #login(Credentials, String)} method, which is why that is the only  * method of this interface.  *<p>  * Starting and stopping ContentRepository instances is the responsibility  * of each particular deployment and not covered by this interface.  * Repository clients should use a deployment-specific mechanism (JNDI,  * OSGi service, etc.) to acquire references to ContentRepository instances.  *<p>  * This interface is thread-safe.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ContentRepository
block|{
comment|/**      * Authenticates a user based on the given credentials or available      * out-of-band information and, if successful, returns a      * {@link ContentSession} instance for accessing repository content      * inside the specified workspace as the authenticated user.      *<p>      * TODO: Determine whether ContentSessions should cover a single      * workspace or the entire repository.      *<p>      * The exact type of access credentials is undefined, as this method      * simply acts as a generic messenger between clients and pluggable      * login modules that take care of the actual authentication. See      * the documentation of relevant login modules for the kind of access      * credentials they expect.      *<p>      * TODO: Instead of the explicit access credentials, should this method      * rather take the arguments to be passed to the relevant      * JAAS {@link javax.security.auth.login.LoginContext} constructor?      *<p>      * The client must explicitly {@link ContentSession#close()} the      * returned session once it is no longer used. The recommended access      * pattern is:      *<pre>      * ContentRepository repository = ...;      * ContentSession session = repository.login(...);      * try {      *     ...; // Use the session      * } finally {      *     session.close();      * }      *</pre>      *      * @param credentials access credentials, or {@code null}      * @param workspaceName      * @return authenticated repository session      * @throws LoginException if authentication failed      * @throws NoSuchWorkspaceException      */
annotation|@
name|Nonnull
name|ContentSession
name|login
parameter_list|(
name|Credentials
name|credentials
parameter_list|,
name|String
name|workspaceName
parameter_list|)
throws|throws
name|LoginException
throws|,
name|NoSuchWorkspaceException
function_decl|;
block|}
end_interface

end_unit

