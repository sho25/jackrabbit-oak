begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|jaas
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|spi
operator|.
name|LoginModule
import|;
end_import

begin_import
import|import
name|aQute
operator|.
name|bnd
operator|.
name|annotation
operator|.
name|ConsumerType
import|;
end_import

begin_comment
comment|/**  * TODO - Taken from Apache Felix JAAS as a temporary mesaure. Should  * be removed once we have a released version of Felix JAAS Bundle  *  * A factory for creating {@link LoginModule} instances.  */
end_comment

begin_interface
annotation|@
name|ConsumerType
specifier|public
interface|interface
name|LoginModuleFactory
block|{
comment|/**      * Property name specifying whether or not a<code>LoginModule</code> is      * REQUIRED, REQUISITE, SUFFICIENT or OPTIONAL. Refer to {@link javax.security.auth.login.Configuration}      * for more details around the meaning of these flags      *      * By default the value is set to REQUIRED      */
name|String
name|JAAS_CONTROL_FLAG
init|=
literal|"jaas.controlFlag"
decl_stmt|;
comment|/**      * Property name specifying the Realm name (or application name) against which the      * LoginModule would be registered.      *      *<p>If no realm name is provided then LoginModule would registered with a default realm      * as configured      */
name|String
name|JAAS_REALM_NAME
init|=
literal|"jaas.realmName"
decl_stmt|;
comment|/**      * Property name specifying the ranking (i.e. sort order) of the configured login module entries. The entries      * are sorted in a descending order (i.e. higher value ranked configurations come first)      * @since 1.0.1 (bundle version 0.0.2)      */
name|String
name|JAAS_RANKING
init|=
literal|"jaas.ranking"
decl_stmt|;
comment|/**      * Creates the LoginModule instance      * @return loginModule instance      */
name|LoginModule
name|createLoginModule
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

