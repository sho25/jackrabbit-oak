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
name|security
operator|.
name|authentication
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
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|jaas
operator|.
name|LoginModuleFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Activate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|ConfigurationPolicy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Deactivate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
import|;
end_import

begin_import
import|import
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
name|ConfigurationParameters
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|ComponentContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * Implements a LoginModuleFactory that creates {  * @link org.apache.jackrabbit.oak.security.authentication.CustomLoginModule}s  * and allows to configure login modules via OSGi config.  */
end_comment

begin_class
annotation|@
name|Component
argument_list|(
name|label
operator|=
literal|"Custom Test Login Module (Oak Exercise Module)"
argument_list|,
name|metatype
operator|=
literal|true
argument_list|,
name|policy
operator|=
name|ConfigurationPolicy
operator|.
name|REQUIRE
argument_list|,
name|configurationFactory
operator|=
literal|true
argument_list|)
annotation|@
name|Service
specifier|public
class|class
name|CustomLoginModuleFactory
implements|implements
name|LoginModuleFactory
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CustomLoginModuleFactory
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
annotation|@
name|Property
argument_list|(
name|intValue
operator|=
literal|500
argument_list|,
name|label
operator|=
literal|"JAAS Ranking"
argument_list|,
name|description
operator|=
literal|"Specifying the ranking (i.e. sort order) of this login module entry. The entries are sorted "
operator|+
literal|"in a descending order (i.e. higher value ranked configurations come first)."
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|JAAS_RANKING
init|=
name|LoginModuleFactory
operator|.
name|JAAS_RANKING
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
annotation|@
name|Property
argument_list|(
name|value
operator|=
literal|"OPTIONAL"
argument_list|,
name|label
operator|=
literal|"JAAS Control Flag"
argument_list|,
name|description
operator|=
literal|"Property specifying whether or not a LoginModule is REQUIRED, REQUISITE, SUFFICIENT or "
operator|+
literal|"OPTIONAL. Refer to the JAAS configuration documentation for more details around the meaning of "
operator|+
literal|"these flags."
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|JAAS_CONTROL_FLAG
init|=
name|LoginModuleFactory
operator|.
name|JAAS_CONTROL_FLAG
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
annotation|@
name|Property
argument_list|(
name|label
operator|=
literal|"JAAS Realm"
argument_list|,
name|description
operator|=
literal|"The realm name (or application name) against which the LoginModule  is be registered. If no "
operator|+
literal|"realm name is provided then LoginModule is registered with a default realm as configured in "
operator|+
literal|"the Felix JAAS configuration."
argument_list|)
specifier|public
specifier|static
specifier|final
name|String
name|JAAS_REALM_NAME
init|=
name|LoginModuleFactory
operator|.
name|JAAS_REALM_NAME
decl_stmt|;
comment|// configuration parameters for the login module instances
specifier|private
name|ConfigurationParameters
name|osgiConfig
decl_stmt|;
comment|/**      * Activates the LoginModuleFactory service      * @param context the component context      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
annotation|@
name|Activate
specifier|private
name|void
name|activate
parameter_list|(
name|ComponentContext
name|componentContext
parameter_list|)
block|{
name|osgiConfig
operator|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|componentContext
operator|.
name|getProperties
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
annotation|@
name|Deactivate
specifier|private
name|void
name|deactivate
parameter_list|()
block|{
comment|// nop
block|}
comment|/**      * {@inheritDoc}      *      * @return a new {@link ExternalLoginModule} instance.      */
annotation|@
name|Override
specifier|public
name|LoginModule
name|createLoginModule
parameter_list|()
block|{
return|return
operator|new
name|CustomLoginModule
argument_list|(
name|osgiConfig
argument_list|)
return|;
block|}
block|}
end_class

end_unit

