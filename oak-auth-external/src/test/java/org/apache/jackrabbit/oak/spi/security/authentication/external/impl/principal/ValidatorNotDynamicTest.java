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
name|CommitFailedException
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

begin_import
import|import static
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
name|CommitFailedException
operator|.
name|CONSTRAINT
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
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|ValidatorNotDynamicTest
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
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isDynamic
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|setExternalPrincipalNames
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
name|systemRoot
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
name|REP_EXTERNAL_PRINCIPAL_NAMES
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"principalName"
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
name|root
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRemoveExternalPrincipalNames
parameter_list|()
throws|throws
name|Exception
block|{
name|setExternalPrincipalNames
argument_list|()
expr_stmt|;
name|super
operator|.
name|testRemoveExternalPrincipalNames
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Test
specifier|public
name|void
name|testRemoveExternalPrincipalNamesAsSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|setExternalPrincipalNames
argument_list|()
expr_stmt|;
name|super
operator|.
name|testRemoveExternalPrincipalNamesAsSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testModifyExternalPrincipalNames
parameter_list|()
throws|throws
name|Exception
block|{
name|setExternalPrincipalNames
argument_list|()
expr_stmt|;
name|super
operator|.
name|testModifyExternalPrincipalNames
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Test
specifier|public
name|void
name|testModifyExternalPrincipalNamesAsSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|setExternalPrincipalNames
argument_list|()
expr_stmt|;
name|super
operator|.
name|testModifyExternalPrincipalNamesAsSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|CommitFailedException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRemoveRepExternalId
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|root
operator|.
name|getTree
argument_list|(
name|externalUserPath
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
name|fail
argument_list|(
literal|"Removal of rep:externalId must be detected in the default setup."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// success: verify nature of the exception
name|assertException
argument_list|(
name|e
argument_list|,
name|CONSTRAINT
argument_list|,
literal|74
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Test
specifier|public
name|void
name|testRemoveRepExternalIdAsSystem
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
name|externalUserPath
argument_list|)
decl_stmt|;
name|userTree
operator|.
name|removeProperty
argument_list|(
name|ExternalIdentityConstants
operator|.
name|REP_EXTERNAL_ID
argument_list|)
expr_stmt|;
name|systemRoot
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

