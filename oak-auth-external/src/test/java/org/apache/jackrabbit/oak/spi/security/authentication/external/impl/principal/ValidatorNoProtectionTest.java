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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|principal
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|ImmutableList
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
name|ImmutableMap
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
name|api
operator|.
name|Root
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
name|api
operator|.
name|Tree
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
name|api
operator|.
name|Type
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
name|authentication
operator|.
name|external
operator|.
name|impl
operator|.
name|ExternalIdentityConstants
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

begin_class
specifier|public
class|class
name|ValidatorNoProtectionTest
extends|extends
name|ExternalIdentityValidatorTest
block|{
annotation|@
name|Override
specifier|public
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|before
argument_list|()
expr_stmt|;
name|ConfigurationParameters
name|params
init|=
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|externalPrincipalConfiguration
operator|.
name|getParameters
argument_list|()
argument_list|,
name|ConfigurationParameters
operator|.
name|of
argument_list|(
name|ExternalIdentityConstants
operator|.
name|PARAM_PROTECT_EXTERNAL_IDS
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|externalPrincipalConfiguration
operator|.
name|setParameters
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Test
specifier|public
name|void
name|testRepExternalIdMultiple
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|Tree
name|userTree
init|=
name|systemRoot
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
decl_stmt|;
name|userTree
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"id"
argument_list|,
literal|"id2"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Test
specifier|public
name|void
name|testRepExternalIdType
parameter_list|()
throws|throws
name|Exception
block|{
name|Root
name|systemRoot
init|=
name|getSystemRoot
argument_list|()
decl_stmt|;
name|Tree
name|userTree
init|=
name|systemRoot
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|Type
argument_list|,
name|Object
argument_list|>
name|valMap
init|=
name|ImmutableMap
operator|.
expr|<
name|Type
decl_stmt|,
name|Object
decl|>
name|of
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|,
name|Type
operator|.
name|LONG
argument_list|,
operator|new
name|Long
argument_list|(
literal|1234
argument_list|)
argument_list|,
name|Type
operator|.
name|NAME
argument_list|,
literal|"id"
argument_list|)
decl_stmt|;
for|for
control|(
name|Type
name|t
range|:
name|valMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|Object
name|val
init|=
name|valMap
operator|.
name|get
argument_list|(
name|t
argument_list|)
decl_stmt|;
try|try
block|{
name|userTree
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
name|val
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|systemRoot
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
annotation|@
name|Test
specifier|public
name|void
name|testAddRepExternalId
parameter_list|()
throws|throws
name|Exception
block|{
name|root
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Test
specifier|public
name|void
name|testModifyRepExternalId
parameter_list|()
throws|throws
name|Exception
block|{
name|root
operator|.
name|getTree
argument_list|(
name|externalUserPath
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
literal|"anotherValue"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Test
specifier|public
name|void
name|testRemoveRepExternalIdWithoutPrincipalNames
parameter_list|()
throws|throws
name|Exception
block|{
name|root
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
operator|.
name|setProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|root
operator|.
name|getTree
argument_list|(
name|testUserPath
argument_list|)
operator|.
name|removeProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

