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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|accesscontrol
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlPolicy
import|;
end_import

begin_comment
comment|/**  * Interface to improve pluggability of the {@link javax.jcr.security.AccessControlManager},  * namely the interaction of multiple managers within a  * single repository. It provides a single method {@link #defines(String, javax.jcr.security.AccessControlPolicy)}  * that allows to termine the responsible manager upon  * {@link javax.jcr.security.AccessControlManager#setPolicy(String, javax.jcr.security.AccessControlPolicy) setPolicy}  * and  * {@link javax.jcr.security.AccessControlManager#removePolicy(String, javax.jcr.security.AccessControlPolicy) removePolicy}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|PolicyOwner
block|{
comment|/**      * Determines if the implementing {@code AccessManager} defines the specified      * {@code acceessControlPolicy} at the given {@code absPath}. If this method      * returns {@code true} it is expected that the given policy is valid to be      * {@link javax.jcr.security.AccessControlManager#setPolicy(String, javax.jcr.security.AccessControlPolicy) set}      * or {@link javax.jcr.security.AccessControlManager#removePolicy(String, javax.jcr.security.AccessControlPolicy) removed}      * with the manager.      *      * @param absPath An absolute path.      * @param accessControlPolicy The access control policy to be tested.      * @return {@code true} If the {@code AccessControlManager} implementing this      * interface can handle the specified {@code accessControlPolicy} at the given {@code path}.      */
name|boolean
name|defines
parameter_list|(
name|String
name|absPath
parameter_list|,
name|AccessControlPolicy
name|accessControlPolicy
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

