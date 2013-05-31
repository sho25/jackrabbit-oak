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
name|nodetype
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
operator|.
name|newHashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeDefinition
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|PropertyDefinition
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
name|JcrConstants
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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|NodeDefinitionTest
extends|extends
name|AbstractJCRTest
block|{
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|paths
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
specifier|public
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
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"a"
argument_list|,
name|JcrConstants
operator|.
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"b"
argument_list|,
name|JcrConstants
operator|.
name|NT_FOLDER
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|paths
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
literal|"/"
argument_list|,
literal|"/jcr:system"
argument_list|,
literal|"/jcr:system/jcr:versionStorage"
argument_list|,
literal|"/jcr:system/jcr:nodeTypes"
argument_list|,
literal|"/jcr:system/rep:namespaces"
argument_list|,
name|testRoot
operator|+
literal|"/a"
argument_list|,
name|testRoot
operator|+
literal|"/b"
argument_list|,
literal|"/oak:index"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetRequiredPrimaryTypes
parameter_list|()
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|Node
name|n
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|NodeDefinition
name|def
init|=
name|n
operator|.
name|getDefinition
argument_list|()
decl_stmt|;
name|def
operator|.
name|getRequiredPrimaryTypes
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetRequiredPrimaryTypes2
parameter_list|()
throws|throws
name|RepositoryException
block|{
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|Node
name|n
init|=
name|superuser
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
decl_stmt|;
for|for
control|(
name|NodeDefinition
name|nd
range|:
name|getAggregatedNodeDefinitions
argument_list|(
name|n
argument_list|)
control|)
block|{
name|nd
operator|.
name|getRequiredPrimaryTypes
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|NodeDefinition
index|[]
name|getAggregatedNodeDefinitions
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Set
argument_list|<
name|NodeDefinition
argument_list|>
name|cDefs
init|=
name|newHashSet
argument_list|()
decl_stmt|;
name|NodeDefinition
index|[]
name|nd
init|=
name|node
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getChildNodeDefinitions
argument_list|()
decl_stmt|;
name|cDefs
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|nd
argument_list|)
argument_list|)
expr_stmt|;
name|NodeType
index|[]
name|mixins
init|=
name|node
operator|.
name|getMixinNodeTypes
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeType
name|mixin
range|:
name|mixins
control|)
block|{
name|nd
operator|=
name|mixin
operator|.
name|getChildNodeDefinitions
argument_list|()
expr_stmt|;
name|cDefs
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|nd
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|cDefs
operator|.
name|toArray
argument_list|(
operator|new
name|NodeDefinition
index|[
name|cDefs
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PropertyDefinition
index|[]
name|getAggregatedPropertyDefinitionss
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Set
argument_list|<
name|PropertyDefinition
argument_list|>
name|pDefs
init|=
name|newHashSet
argument_list|()
decl_stmt|;
name|PropertyDefinition
name|pd
index|[]
init|=
name|node
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|getPropertyDefinitions
argument_list|()
decl_stmt|;
name|pDefs
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|pd
argument_list|)
argument_list|)
expr_stmt|;
name|NodeType
index|[]
name|mixins
init|=
name|node
operator|.
name|getMixinNodeTypes
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeType
name|mixin
range|:
name|mixins
control|)
block|{
name|pd
operator|=
name|mixin
operator|.
name|getPropertyDefinitions
argument_list|()
expr_stmt|;
name|pDefs
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|pd
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|pDefs
operator|.
name|toArray
argument_list|(
operator|new
name|PropertyDefinition
index|[
name|pDefs
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

