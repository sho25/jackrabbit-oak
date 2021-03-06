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
name|jcr
operator|.
name|version
package|;
end_package

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
name|Property
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|OnParentVersionAction
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|version
operator|.
name|VersionManager
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
name|nodetype
operator|.
name|NodeTypeConstants
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
name|version
operator|.
name|VersionConstants
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
name|test
operator|.
name|AbstractJCRTest
import|;
end_import

begin_comment
comment|/**  * Test OPV VERSION  */
end_comment

begin_class
specifier|public
class|class
name|OpvVersionTest
extends|extends
name|AbstractJCRTest
implements|implements
name|VersionConstants
block|{
specifier|private
name|String
name|siblingName
decl_stmt|;
specifier|private
name|VersionManager
name|versionManager
decl_stmt|;
specifier|private
name|Node
name|frozen
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|Node
name|n1
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|nodeName1
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Node
name|n2
init|=
name|n1
operator|.
name|addNode
argument_list|(
name|nodeName2
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Node
name|n3
init|=
name|n1
operator|.
name|addNode
argument_list|(
name|nodeName3
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|siblingName
operator|=
name|nodeName1
operator|+
literal|'b'
expr_stmt|;
name|Node
name|n1b
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
name|siblingName
argument_list|,
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|OnParentVersionAction
operator|.
name|VERSION
argument_list|,
name|n1
operator|.
name|getDefinition
argument_list|()
operator|.
name|getOnParentVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OnParentVersionAction
operator|.
name|VERSION
argument_list|,
name|n2
operator|.
name|getDefinition
argument_list|()
operator|.
name|getOnParentVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OnParentVersionAction
operator|.
name|VERSION
argument_list|,
name|n3
operator|.
name|getDefinition
argument_list|()
operator|.
name|getOnParentVersion
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|OnParentVersionAction
operator|.
name|VERSION
argument_list|,
name|n1b
operator|.
name|getDefinition
argument_list|()
operator|.
name|getOnParentVersion
argument_list|()
argument_list|)
expr_stmt|;
name|testRootNode
operator|.
name|addMixin
argument_list|(
name|MIX_VERSIONABLE
argument_list|)
expr_stmt|;
name|n1b
operator|.
name|addMixin
argument_list|(
name|MIX_VERSIONABLE
argument_list|)
expr_stmt|;
name|n2
operator|.
name|addMixin
argument_list|(
name|MIX_VERSIONABLE
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|versionManager
operator|=
name|superuser
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getVersionManager
argument_list|()
expr_stmt|;
name|frozen
operator|=
name|versionManager
operator|.
name|checkpoint
argument_list|(
name|testRoot
argument_list|)
operator|.
name|getFrozenNode
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testDirectChild
parameter_list|()
throws|throws
name|Exception
block|{
comment|// n1 : is not versionable -> copied to frozen node
name|assertTrue
argument_list|(
name|frozen
operator|.
name|hasNode
argument_list|(
name|nodeName1
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|frozenN1
init|=
name|frozen
operator|.
name|getNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NT_FROZENNODE
argument_list|,
name|frozenN1
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|frozenN1
operator|.
name|hasNode
argument_list|(
name|nodeName2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|frozenN1
operator|.
name|hasNode
argument_list|(
name|nodeName3
argument_list|)
argument_list|)
expr_stmt|;
comment|// n1b is versionable -> only child of 'nt:versionedChild' is created in
comment|// the frozen node with 'jcr:childVersionHistory' property referring to
comment|// the version history of n1b
name|assertTrue
argument_list|(
name|frozen
operator|.
name|hasNode
argument_list|(
name|siblingName
argument_list|)
argument_list|)
expr_stmt|;
name|Node
name|frozenN1b
init|=
name|frozen
operator|.
name|getNode
argument_list|(
name|siblingName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NT_VERSIONEDCHILD
argument_list|,
name|frozenN1b
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Property
name|childVh
init|=
name|frozenN1b
operator|.
name|getProperty
argument_list|(
name|JCR_CHILD_VERSION_HISTORY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|testRoot
operator|+
literal|'/'
operator|+
name|siblingName
argument_list|)
operator|.
name|getUUID
argument_list|()
argument_list|,
name|childVh
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testChildInSubTree
parameter_list|()
throws|throws
name|Exception
block|{
name|Node
name|frozenN1
init|=
name|frozen
operator|.
name|getNode
argument_list|(
name|nodeName1
argument_list|)
decl_stmt|;
name|Node
name|frozenN2
init|=
name|frozenN1
operator|.
name|getNode
argument_list|(
name|nodeName2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NT_VERSIONEDCHILD
argument_list|,
name|frozenN2
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Property
name|childVh
init|=
name|frozenN2
operator|.
name|getProperty
argument_list|(
name|JCR_CHILD_VERSION_HISTORY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|versionManager
operator|.
name|getVersionHistory
argument_list|(
name|testRoot
operator|+
literal|'/'
operator|+
name|nodeName1
operator|+
literal|'/'
operator|+
name|nodeName2
argument_list|)
operator|.
name|getUUID
argument_list|()
argument_list|,
name|childVh
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|Node
name|frozenN3
init|=
name|frozenN1
operator|.
name|getNode
argument_list|(
name|nodeName3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|NT_FROZENNODE
argument_list|,
name|frozenN3
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

