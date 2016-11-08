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
name|user
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|AbstractSecurityTest
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
name|commit
operator|.
name|MoveTracker
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
name|commit
operator|.
name|ValidatorProvider
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
name|user
operator|.
name|UserConfiguration
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
name|user
operator|.
name|UserConstants
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
name|user
operator|.
name|util
operator|.
name|PasswordUtil
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
name|xml
operator|.
name|ImportBehavior
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
name|xml
operator|.
name|ProtectedItemImporter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|UserConfigurationImplTest
extends|extends
name|AbstractSecurityTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|USER_PATH
init|=
literal|"/this/is/a/user/test"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|GROUP_PATH
init|=
literal|"/this/is/a/group/test"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Integer
name|DEFAULT_DEPTH
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|IMPORT_BEHAVIOR
init|=
name|ImportBehavior
operator|.
name|NAME_BESTEFFORT
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HASH_ALGORITHM
init|=
literal|"MD5"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Integer
name|HASH_ITERATIONS
init|=
literal|500
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Integer
name|SALT_SIZE
init|=
literal|6
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|SUPPORT_AUTOSAVE
init|=
literal|true
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Integer
name|MAX_AGE
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|INITIAL_PASSWORD_CHANGE
init|=
literal|true
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Integer
name|PASSWORD_HISTORY_SIZE
init|=
literal|12
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|ENABLE_RFC7613_USERCASE_MAPPED_PROFILE
init|=
literal|true
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|ConfigurationParameters
name|getSecurityConfigParameters
parameter_list|()
block|{
return|return
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|UserConfiguration
operator|.
name|NAME
argument_list|,
name|getParams
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidators
parameter_list|()
block|{
name|UserConfigurationImpl
name|configuration
init|=
operator|new
name|UserConfigurationImpl
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|?
extends|extends
name|ValidatorProvider
argument_list|>
name|validators
init|=
name|configuration
operator|.
name|getValidators
argument_list|(
name|adminSession
operator|.
name|getWorkspaceName
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|Principal
operator|>
name|emptySet
argument_list|()
argument_list|,
operator|new
name|MoveTracker
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|validators
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|clNames
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|UserValidatorProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|CacheValidatorProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|ValidatorProvider
name|vp
range|:
name|validators
control|)
block|{
name|clNames
operator|.
name|remove
argument_list|(
name|vp
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|clNames
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserConfigurationWithConstructor
parameter_list|()
throws|throws
name|Exception
block|{
name|UserConfigurationImpl
name|userConfiguration
init|=
operator|new
name|UserConfigurationImpl
argument_list|(
name|getSecurityProvider
argument_list|()
argument_list|)
decl_stmt|;
name|testConfigurationParameters
argument_list|(
name|userConfiguration
operator|.
name|getParameters
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUserConfigurationWithSetParameters
parameter_list|()
throws|throws
name|Exception
block|{
name|UserConfigurationImpl
name|userConfiguration
init|=
operator|new
name|UserConfigurationImpl
argument_list|()
decl_stmt|;
name|userConfiguration
operator|.
name|setParameters
argument_list|(
name|getParams
argument_list|()
argument_list|)
expr_stmt|;
name|testConfigurationParameters
argument_list|(
name|userConfiguration
operator|.
name|getParameters
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testConfigurationParameters
parameter_list|(
name|ConfigurationParameters
name|parameters
parameter_list|)
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_USER_PATH
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_USER_PATH
argument_list|)
argument_list|,
name|USER_PATH
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_GROUP_PATH
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_GROUP_PATH
argument_list|)
argument_list|,
name|GROUP_PATH
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_DEFAULT_DEPTH
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_DEPTH
argument_list|)
argument_list|,
name|DEFAULT_DEPTH
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|ProtectedItemImporter
operator|.
name|PARAM_IMPORT_BEHAVIOR
argument_list|,
name|ImportBehavior
operator|.
name|NAME_IGNORE
argument_list|)
argument_list|,
name|IMPORT_BEHAVIOR
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_HASH_ALGORITHM
argument_list|,
name|PasswordUtil
operator|.
name|DEFAULT_ALGORITHM
argument_list|)
argument_list|,
name|HASH_ALGORITHM
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_HASH_ITERATIONS
argument_list|,
name|PasswordUtil
operator|.
name|DEFAULT_ITERATIONS
argument_list|)
argument_list|,
name|HASH_ITERATIONS
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_SALT_SIZE
argument_list|,
name|PasswordUtil
operator|.
name|DEFAULT_SALT_SIZE
argument_list|)
argument_list|,
name|SALT_SIZE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_SUPPORT_AUTOSAVE
argument_list|,
literal|false
argument_list|)
argument_list|,
name|SUPPORT_AUTOSAVE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_MAX_AGE
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_PASSWORD_MAX_AGE
argument_list|)
argument_list|,
name|MAX_AGE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_INITIAL_CHANGE
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_PASSWORD_INITIAL_CHANGE
argument_list|)
argument_list|,
name|INITIAL_PASSWORD_CHANGE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_HISTORY_SIZE
argument_list|,
name|UserConstants
operator|.
name|PASSWORD_HISTORY_DISABLED_SIZE
argument_list|)
argument_list|,
name|PASSWORD_HISTORY_SIZE
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parameters
operator|.
name|getConfigValue
argument_list|(
name|UserConstants
operator|.
name|PARAM_ENABLE_RFC7613_USERCASE_MAPPED_PROFILE
argument_list|,
name|UserConstants
operator|.
name|DEFAULT_ENABLE_RFC7613_USERCASE_MAPPED_PROFILE
argument_list|)
argument_list|,
name|ENABLE_RFC7613_USERCASE_MAPPED_PROFILE
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ConfigurationParameters
name|getParams
parameter_list|()
block|{
name|ConfigurationParameters
name|params
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
block|{
name|put
parameter_list|(
name|UserConstants
operator|.
name|PARAM_USER_PATH
parameter_list|,
name|USER_PATH
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|UserConstants
operator|.
name|PARAM_GROUP_PATH
parameter_list|,
name|GROUP_PATH
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|UserConstants
operator|.
name|PARAM_DEFAULT_DEPTH
parameter_list|,
name|DEFAULT_DEPTH
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|ProtectedItemImporter
operator|.
name|PARAM_IMPORT_BEHAVIOR
parameter_list|,
name|IMPORT_BEHAVIOR
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_HASH_ALGORITHM
parameter_list|,
name|HASH_ALGORITHM
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_HASH_ITERATIONS
parameter_list|,
name|HASH_ITERATIONS
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_SALT_SIZE
parameter_list|,
name|SALT_SIZE
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|UserConstants
operator|.
name|PARAM_SUPPORT_AUTOSAVE
parameter_list|,
name|SUPPORT_AUTOSAVE
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_MAX_AGE
parameter_list|,
name|MAX_AGE
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_INITIAL_CHANGE
parameter_list|,
name|INITIAL_PASSWORD_CHANGE
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|UserConstants
operator|.
name|PARAM_PASSWORD_HISTORY_SIZE
parameter_list|,
name|PASSWORD_HISTORY_SIZE
parameter_list|)
constructor_decl|;
name|put
parameter_list|(
name|UserConstants
operator|.
name|PARAM_ENABLE_RFC7613_USERCASE_MAPPED_PROFILE
parameter_list|,
name|ENABLE_RFC7613_USERCASE_MAPPED_PROFILE
parameter_list|)
constructor_decl|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
name|params
return|;
block|}
block|}
end_class

end_unit

