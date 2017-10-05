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
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|property
package|;
end_package

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
name|Iterators
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
name|commons
operator|.
name|PathUtils
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
name|plugins
operator|.
name|index
operator|.
name|Cursors
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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|IndexDefinition
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
name|plugins
operator|.
name|index
operator|.
name|lucene
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
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|util
operator|.
name|IndexDefinitionBuilder
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
name|query
operator|.
name|NodeStateNodeTypeInfoProvider
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
name|query
operator|.
name|QueryEngineSettings
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
name|query
operator|.
name|ast
operator|.
name|NodeTypeInfo
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
name|query
operator|.
name|ast
operator|.
name|NodeTypeInfoProvider
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
name|query
operator|.
name|ast
operator|.
name|Operator
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
name|query
operator|.
name|ast
operator|.
name|SelectorImpl
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
name|query
operator|.
name|index
operator|.
name|FilterImpl
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
name|query
operator|.
name|Cursor
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
name|query
operator|.
name|Filter
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
name|state
operator|.
name|NodeBuilder
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
name|state
operator|.
name|NodeState
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
name|InitialContent
operator|.
name|INITIAL_CONTENT
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
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
operator|.
name|createProperty
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
name|plugins
operator|.
name|memory
operator|.
name|PropertyValues
operator|.
name|newString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|containsInAnyOrder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|empty
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
name|assertThat
import|;
end_import

begin_class
specifier|public
class|class
name|HybridPropertyIndexLookupTest
block|{
specifier|private
name|NodeState
name|root
init|=
name|INITIAL_CONTENT
decl_stmt|;
specifier|private
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|private
name|IndexDefinitionBuilder
name|defnb
init|=
operator|new
name|IndexDefinitionBuilder
argument_list|()
decl_stmt|;
specifier|private
name|String
name|indexPath
init|=
literal|"/oak:index/foo"
decl_stmt|;
specifier|private
name|PropertyIndexUpdateCallback
name|callback
init|=
operator|new
name|PropertyIndexUpdateCallback
argument_list|(
name|indexPath
argument_list|,
name|builder
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|simplePropertyRestriction
parameter_list|()
throws|throws
name|Exception
block|{
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|sync
argument_list|()
expr_stmt|;
name|propertyUpdated
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|FilterImpl
name|f
init|=
name|createFilter
argument_list|()
decl_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|(
name|f
argument_list|,
literal|"foo"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|valuePattern
parameter_list|()
throws|throws
name|Exception
block|{
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|sync
argument_list|()
operator|.
name|valuePattern
argument_list|(
literal|"(a.*|b)"
argument_list|)
expr_stmt|;
name|propertyUpdated
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|propertyUpdated
argument_list|(
literal|"/a1"
argument_list|,
literal|"foo"
argument_list|,
literal|"a1"
argument_list|)
expr_stmt|;
name|propertyUpdated
argument_list|(
literal|"/b"
argument_list|,
literal|"foo"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|propertyUpdated
argument_list|(
literal|"/c"
argument_list|,
literal|"foo"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|(
literal|"foo"
argument_list|,
literal|"a"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|(
literal|"foo"
argument_list|,
literal|"a1"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/a1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|(
literal|"foo"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/b"
argument_list|)
argument_list|)
expr_stmt|;
comment|// c should not be found as its excluded
name|assertThat
argument_list|(
name|query
argument_list|(
literal|"foo"
argument_list|,
literal|"c"
argument_list|)
argument_list|,
name|empty
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|relativeProperty
parameter_list|()
throws|throws
name|Exception
block|{
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|sync
argument_list|()
expr_stmt|;
name|propertyUpdated
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|FilterImpl
name|f
init|=
name|createFilter
argument_list|()
decl_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"jcr:content/foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|query
argument_list|(
name|f
argument_list|,
literal|"foo"
argument_list|,
literal|"jcr:content/foo"
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|pathResultAbsolutePath
parameter_list|()
throws|throws
name|Exception
block|{
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|sync
argument_list|()
expr_stmt|;
name|propertyUpdated
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|String
name|propertyName
init|=
literal|"foo"
decl_stmt|;
name|FilterImpl
name|filter
init|=
name|createFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|HybridPropertyIndexLookup
name|lookup
init|=
operator|new
name|HybridPropertyIndexLookup
argument_list|(
name|indexPath
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|lookup
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|pd
argument_list|(
name|propertyName
argument_list|)
argument_list|,
name|propertyName
argument_list|,
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
name|propertyName
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|paths
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|nonRootIndex
parameter_list|()
throws|throws
name|Exception
block|{
name|defnb
operator|.
name|indexRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|property
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|sync
argument_list|()
expr_stmt|;
name|indexPath
operator|=
literal|"/content/oak:index/fooIndex"
expr_stmt|;
name|propertyUpdated
argument_list|(
literal|"/a"
argument_list|,
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|String
name|propertyName
init|=
literal|"foo"
decl_stmt|;
name|FilterImpl
name|filter
init|=
name|createFilter
argument_list|()
decl_stmt|;
name|filter
operator|.
name|restrictProperty
argument_list|(
literal|"foo"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|newString
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|filter
operator|.
name|restrictPath
argument_list|(
literal|"/content"
argument_list|,
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
expr_stmt|;
name|HybridPropertyIndexLookup
name|lookup
init|=
operator|new
name|HybridPropertyIndexLookup
argument_list|(
name|indexPath
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/content"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|lookup
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|pd
argument_list|(
name|propertyName
argument_list|)
argument_list|,
name|propertyName
argument_list|,
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
name|propertyName
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|paths
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/a"
argument_list|)
argument_list|)
expr_stmt|;
name|lookup
operator|=
operator|new
name|HybridPropertyIndexLookup
argument_list|(
name|indexPath
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
literal|"/content"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|paths
operator|=
name|lookup
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|pd
argument_list|(
name|propertyName
argument_list|)
argument_list|,
name|propertyName
argument_list|,
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
name|propertyName
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|paths
argument_list|)
argument_list|,
name|containsInAnyOrder
argument_list|(
literal|"/content/a"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|propertyUpdated
parameter_list|(
name|String
name|nodePath
parameter_list|,
name|String
name|propertyRelativeName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|callback
operator|.
name|propertyUpdated
argument_list|(
name|nodePath
argument_list|,
name|propertyRelativeName
argument_list|,
name|pd
argument_list|(
name|propertyRelativeName
argument_list|)
argument_list|,
literal|null
argument_list|,
name|createProperty
argument_list|(
name|PathUtils
operator|.
name|getName
argument_list|(
name|propertyRelativeName
argument_list|)
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|query
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|FilterImpl
name|f
init|=
name|createFilter
argument_list|()
decl_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
name|propertyName
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|newString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|query
argument_list|(
name|f
argument_list|,
name|propertyName
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|query
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|propertyName
parameter_list|)
block|{
return|return
name|query
argument_list|(
name|filter
argument_list|,
name|propertyName
argument_list|,
name|propertyName
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|query
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|String
name|propertyRestrictionName
parameter_list|)
block|{
name|HybridPropertyIndexLookup
name|lookup
init|=
operator|new
name|HybridPropertyIndexLookup
argument_list|(
name|indexPath
argument_list|,
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|lookup
operator|.
name|query
argument_list|(
name|filter
argument_list|,
name|pd
argument_list|(
name|propertyName
argument_list|)
argument_list|,
name|propertyName
argument_list|,
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
name|propertyRestrictionName
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|paths
argument_list|)
return|;
block|}
specifier|private
name|PropertyDefinition
name|pd
parameter_list|(
name|String
name|propName
parameter_list|)
block|{
name|IndexDefinition
name|defn
init|=
operator|new
name|IndexDefinition
argument_list|(
name|root
argument_list|,
name|defnb
operator|.
name|build
argument_list|()
argument_list|,
name|indexPath
argument_list|)
decl_stmt|;
return|return
name|defn
operator|.
name|getApplicableIndexingRule
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|getConfig
argument_list|(
name|propName
argument_list|)
return|;
block|}
specifier|private
name|FilterImpl
name|createFilter
parameter_list|()
block|{
return|return
name|createFilter
argument_list|(
name|root
argument_list|,
literal|"nt:base"
argument_list|)
return|;
block|}
specifier|private
name|FilterImpl
name|createFilter
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|nodeTypeName
parameter_list|)
block|{
name|NodeTypeInfoProvider
name|nodeTypes
init|=
operator|new
name|NodeStateNodeTypeInfoProvider
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|NodeTypeInfo
name|type
init|=
name|nodeTypes
operator|.
name|getNodeTypeInfo
argument_list|(
name|nodeTypeName
argument_list|)
decl_stmt|;
name|SelectorImpl
name|selector
init|=
operator|new
name|SelectorImpl
argument_list|(
name|type
argument_list|,
name|nodeTypeName
argument_list|)
decl_stmt|;
return|return
operator|new
name|FilterImpl
argument_list|(
name|selector
argument_list|,
literal|"SELECT * FROM ["
operator|+
name|nodeTypeName
operator|+
literal|"]"
argument_list|,
operator|new
name|QueryEngineSettings
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit
