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
name|cug
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Value
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
name|Sets
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
name|principal
operator|.
name|EveryonePrincipal
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|CugImportBesteffortTest
extends|extends
name|CugImportBaseTest
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|PRINCIPAL_NAMES
init|=
name|Sets
operator|.
name|newHashSet
argument_list|(
name|EveryonePrincipal
operator|.
name|NAME
argument_list|,
name|TEST_GROUP_PRINCIPAL_NAME
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|String
name|getImportBehavior
parameter_list|()
block|{
return|return
name|ImportBehavior
operator|.
name|NAME_BESTEFFORT
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCugInvalidPrincipals
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|targetNode
init|=
name|getTargetNode
argument_list|()
decl_stmt|;
name|targetNode
operator|.
name|addMixin
argument_list|(
name|CugConstants
operator|.
name|MIX_REP_CUG_MIXIN
argument_list|)
expr_stmt|;
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|XML_CUG_POLICY
argument_list|)
expr_stmt|;
name|Node
name|cugNode
init|=
name|targetNode
operator|.
name|getNode
argument_list|(
name|CugConstants
operator|.
name|REP_CUG_POLICY
argument_list|)
decl_stmt|;
name|Value
index|[]
name|principalNames
init|=
name|cugNode
operator|.
name|getProperty
argument_list|(
name|CugConstants
operator|.
name|REP_PRINCIPAL_NAMES
argument_list|)
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|assertPrincipalNames
argument_list|(
name|PRINCIPAL_NAMES
argument_list|,
name|principalNames
argument_list|)
expr_stmt|;
name|getImportSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNodeWithCugInvalidPrincipals
parameter_list|()
throws|throws
name|Exception
block|{
name|doImport
argument_list|(
name|getTargetPath
argument_list|()
argument_list|,
name|XML_CHILD_WITH_CUG
argument_list|)
expr_stmt|;
name|Node
name|cugNode
init|=
name|getTargetNode
argument_list|()
operator|.
name|getNode
argument_list|(
literal|"child"
argument_list|)
operator|.
name|getNode
argument_list|(
name|CugConstants
operator|.
name|REP_CUG_POLICY
argument_list|)
decl_stmt|;
name|Value
index|[]
name|principalNames
init|=
name|cugNode
operator|.
name|getProperty
argument_list|(
name|CugConstants
operator|.
name|REP_PRINCIPAL_NAMES
argument_list|)
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|assertPrincipalNames
argument_list|(
name|PRINCIPAL_NAMES
argument_list|,
name|principalNames
argument_list|)
expr_stmt|;
name|getImportSession
argument_list|()
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

