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
name|composite
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
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
name|Type
operator|.
name|NAME
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
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
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
name|index
operator|.
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NODE_TYPE
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
name|index
operator|.
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
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
name|index
operator|.
name|IndexConstants
operator|.
name|REINDEX_PROPERTY_NAME
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
name|index
operator|.
name|IndexConstants
operator|.
name|REINDEX_COUNT
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
name|index
operator|.
name|IndexUtils
operator|.
name|createIndexDefinition
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
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
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
name|assertThat
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|Query
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
name|plugins
operator|.
name|index
operator|.
name|IndexConstants
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
name|IndexUpdateProvider
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
name|LuceneIndexConstants
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
name|LuceneIndexEditorProvider
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
name|property
operator|.
name|PropertyIndexEditorProvider
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
name|reference
operator|.
name|NodeReferenceConstants
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
name|reference
operator|.
name|ReferenceEditorProvider
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
name|search
operator|.
name|FulltextIndexConstants
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
name|search
operator|.
name|IndexFormatVersion
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
name|CommitInfo
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
name|EditorHook
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
name|junit
operator|.
name|Ignore
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
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
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
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|jersey
operator|.
name|repackaged
operator|.
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

begin_comment
comment|/**  * Tests indexing and queries when using the composite node store.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|CompositeNodeStoreQueryTest
extends|extends
name|CompositeNodeStoreQueryTestBase
block|{
specifier|public
name|CompositeNodeStoreQueryTest
parameter_list|(
name|NodeStoreKind
name|root
parameter_list|,
name|NodeStoreKind
name|mounts
parameter_list|)
block|{
name|super
argument_list|(
name|root
argument_list|,
name|mounts
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|propertyIndex
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create an index in both the read-only and the read-write store
name|NodeBuilder
name|b
decl_stmt|;
name|NodeBuilder
name|readOnlyBuilder
init|=
name|readOnlyStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b
operator|=
name|createIndexDefinition
argument_list|(
name|readOnlyBuilder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"foo"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"excludedPaths"
argument_list|,
literal|"/jcr:system"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|globalBuilder
init|=
name|globalStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b
operator|=
name|createIndexDefinition
argument_list|(
name|globalBuilder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
literal|"foo"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"foo"
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"excludedPaths"
argument_list|,
literal|"/jcr:system"
argument_list|)
expr_stmt|;
name|EditorHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
operator|new
name|PropertyIndexEditorProvider
argument_list|()
operator|.
name|with
argument_list|(
name|mip
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|readOnlyStore
operator|.
name|merge
argument_list|(
name|readOnlyBuilder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|globalStore
operator|.
name|merge
argument_list|(
name|globalBuilder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// add nodes in the read-only area
name|NodeBuilder
name|builder
decl_stmt|;
name|builder
operator|=
name|readOnlyStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"readOnly"
argument_list|)
operator|.
name|child
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
block|}
name|readOnlyStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// run a query
comment|// need to login again to see changes in the read-only area
name|session
operator|=
name|createRepository
argument_list|(
name|store
argument_list|)
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|root
operator|=
name|session
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|qe
operator|=
name|root
operator|.
name|getQueryEngine
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|executeQuery
argument_list|(
literal|"explain /jcr:root//*[@foo = 'bar']"
argument_list|,
literal|"xpath"
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"/* property foo = bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[/readOnly/node-0, /readOnly/node-1, /readOnly/node-2]"
argument_list|,
name|executeQuery
argument_list|(
literal|"/jcr:root//*[@foo = 'bar']"
argument_list|,
literal|"xpath"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// add nodes in the read-write area
name|builder
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"content"
argument_list|)
operator|.
name|child
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// run a query
name|assertEquals
argument_list|(
literal|"[/content/node-0, /content/node-1, /content/node-2, "
operator|+
literal|"/readOnly/node-0, /readOnly/node-1, /readOnly/node-2]"
argument_list|,
name|executeQuery
argument_list|(
literal|"/jcr:root//*[@foo = 'bar']"
argument_list|,
literal|"xpath"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|executeQuery
argument_list|(
literal|"explain /jcr:root/content//*[@foo = 'bar']"
argument_list|,
literal|"xpath"
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"/* property foo = bar"
argument_list|)
argument_list|)
expr_stmt|;
comment|// remove all data
name|builder
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"content"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// run a query
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|executeQuery
argument_list|(
literal|"/jcr:root/content//*[@foo = 'bar']"
argument_list|,
literal|"xpath"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"OAK-7995"
argument_list|)
specifier|public
name|void
name|referenceIndex
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create an index in both the read-only and the read-write store
name|NodeBuilder
name|b
decl_stmt|;
name|NodeBuilder
name|readOnlyBuilder
init|=
name|readOnlyStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b
operator|=
name|readOnlyBuilder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|child
argument_list|(
literal|"reference"
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|,
name|NodeReferenceConstants
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|NodeBuilder
name|globalBuilder
init|=
name|globalStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b
operator|=
name|globalBuilder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|child
argument_list|(
literal|"reference"
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|,
name|NodeReferenceConstants
operator|.
name|TYPE
argument_list|)
expr_stmt|;
name|EditorHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
operator|new
name|ReferenceEditorProvider
argument_list|()
operator|.
name|with
argument_list|(
name|mip
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|readOnlyStore
operator|.
name|merge
argument_list|(
name|readOnlyBuilder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|globalStore
operator|.
name|merge
argument_list|(
name|globalBuilder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|NodeBuilder
name|builder
decl_stmt|;
name|builder
operator|=
name|readOnlyStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"readOnly"
argument_list|)
operator|.
name|child
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"u1"
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|readOnlyStore
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|builder
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"u1"
argument_list|,
name|Type
operator|.
name|REFERENCE
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// run a query
comment|// need to login again to see changes in the read-only area
name|session
operator|=
name|createRepository
argument_list|(
name|store
argument_list|)
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|root
operator|=
name|session
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|qe
operator|=
name|root
operator|.
name|getQueryEngine
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|executeQuery
argument_list|(
literal|"explain select * from [nt:base] "
operator|+
literal|"where property([*], 'Reference') = cast('u1' as reference)"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"/* reference "
argument_list|)
argument_list|)
expr_stmt|;
comment|// expected: also /readOnly/node-0 .. 2
name|assertEquals
argument_list|(
literal|"[/a/x, /readOnly/node-0, /readOnly/node-1, /readOnly/node-2]"
argument_list|,
name|executeQuery
argument_list|(
literal|"select [jcr:path] from [nt:base] "
operator|+
literal|"where property([*], 'Reference') = cast('u1' as reference)"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createLuceneIndex
parameter_list|(
name|NodeBuilder
name|b
parameter_list|)
block|{
name|b
operator|=
name|b
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|child
argument_list|(
literal|"lucene"
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|INDEX_DEFINITIONS_NODE_TYPE
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
name|TYPE_PROPERTY_NAME
argument_list|,
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|COMPAT_MODE
argument_list|,
name|IndexFormatVersion
operator|.
name|V2
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
name|IndexConstants
operator|.
name|ASYNC_PROPERTY_NAME
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
literal|"async"
argument_list|,
literal|"nrt"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"excludedPaths"
argument_list|,
literal|"/jcr:system"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|foo
init|=
name|b
operator|.
name|child
argument_list|(
name|FulltextIndexConstants
operator|.
name|INDEX_RULES
argument_list|)
operator|.
name|child
argument_list|(
literal|"nt:base"
argument_list|)
operator|.
name|child
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_NODE
argument_list|)
operator|.
name|child
argument_list|(
literal|"asyncFoo"
argument_list|)
decl_stmt|;
name|foo
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_NAME
argument_list|,
literal|"asyncFoo"
argument_list|)
expr_stmt|;
name|foo
operator|.
name|setProperty
argument_list|(
name|FulltextIndexConstants
operator|.
name|PROP_PROPERTY_INDEX
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|createAndReindex
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create an index in both the read-only and the read-write store
name|NodeBuilder
name|readOnlyBuilder
init|=
name|readOnlyStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// add nodes in the read-only area
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|NodeBuilder
name|b
init|=
name|readOnlyBuilder
operator|.
name|child
argument_list|(
literal|"readOnly"
argument_list|)
operator|.
name|child
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"asyncFoo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:base"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
name|createLuceneIndex
argument_list|(
name|readOnlyBuilder
argument_list|)
expr_stmt|;
name|NodeBuilder
name|globalBuilder
init|=
name|globalStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createLuceneIndex
argument_list|(
name|globalBuilder
argument_list|)
expr_stmt|;
name|LuceneIndexEditorProvider
name|iep
init|=
operator|new
name|LuceneIndexEditorProvider
argument_list|(
name|indexCopier
argument_list|,
name|indexTracker
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|mip
argument_list|)
decl_stmt|;
name|EditorHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
name|iep
argument_list|,
literal|"async"
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|readOnlyStore
operator|.
name|merge
argument_list|(
name|readOnlyBuilder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|globalStore
operator|.
name|merge
argument_list|(
name|globalBuilder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|indexTracker
operator|.
name|update
argument_list|(
name|readOnlyStore
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|indexTracker
operator|.
name|update
argument_list|(
name|globalStore
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
comment|//reindex
name|NodeBuilder
name|builder
decl_stmt|;
name|builder
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|child
argument_list|(
literal|"lucene"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|child
argument_list|(
literal|"lucene"
argument_list|)
operator|.
name|getProperty
argument_list|(
name|REINDEX_COUNT
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|luceneIndex
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create an index in both the read-only and the read-write store
name|NodeBuilder
name|readOnlyBuilder
init|=
name|readOnlyStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// add nodes in the read-only area
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|NodeBuilder
name|b
init|=
name|readOnlyBuilder
operator|.
name|child
argument_list|(
literal|"readOnly"
argument_list|)
operator|.
name|child
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
decl_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"asyncFoo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|b
operator|.
name|setProperty
argument_list|(
literal|"jcr:primaryType"
argument_list|,
literal|"nt:base"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
name|createLuceneIndex
argument_list|(
name|readOnlyBuilder
argument_list|)
expr_stmt|;
name|NodeBuilder
name|globalBuilder
init|=
name|globalStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|createLuceneIndex
argument_list|(
name|globalBuilder
argument_list|)
expr_stmt|;
name|LuceneIndexEditorProvider
name|iep
init|=
operator|new
name|LuceneIndexEditorProvider
argument_list|(
name|indexCopier
argument_list|,
name|indexTracker
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|mip
argument_list|)
decl_stmt|;
name|EditorHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|IndexUpdateProvider
argument_list|(
name|iep
argument_list|,
literal|"async"
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|readOnlyStore
operator|.
name|merge
argument_list|(
name|readOnlyBuilder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|globalStore
operator|.
name|merge
argument_list|(
name|globalBuilder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|indexTracker
operator|.
name|update
argument_list|(
name|readOnlyStore
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|indexTracker
operator|.
name|update
argument_list|(
name|globalStore
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
comment|// run a query
comment|// need to login again to see changes in the read-only area
name|session
operator|=
name|createRepository
argument_list|(
name|store
argument_list|)
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|root
operator|=
name|session
operator|.
name|getLatestRoot
argument_list|()
expr_stmt|;
name|qe
operator|=
name|root
operator|.
name|getQueryEngine
argument_list|()
expr_stmt|;
name|indexTracker
operator|.
name|update
argument_list|(
name|store
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|executeQuery
argument_list|(
literal|"explain /jcr:root//*[@asyncFoo = 'bar']"
argument_list|,
literal|"xpath"
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"/* lucene:lucene(/oak:index/lucene) asyncFoo:bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[/readOnly/node-0, /readOnly/node-1, /readOnly/node-2]"
argument_list|,
name|executeQuery
argument_list|(
literal|"/jcr:root//*[@asyncFoo = 'bar']"
argument_list|,
literal|"xpath"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// add nodes in the read-write area
name|NodeBuilder
name|builder
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|child
argument_list|(
literal|"content"
argument_list|)
operator|.
name|child
argument_list|(
literal|"node-"
operator|+
name|i
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"asyncFoo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
block|}
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// run a query
name|assertThat
argument_list|(
name|executeQuery
argument_list|(
literal|"explain /jcr:root//*[@asyncFoo = 'bar']"
argument_list|,
literal|"xpath"
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|containsString
argument_list|(
literal|"/* lucene:lucene(/oak:index/lucene) asyncFoo:bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[/content/node-0, /content/node-1, /content/node-2, "
operator|+
literal|"/readOnly/node-0, /readOnly/node-1, /readOnly/node-2]"
argument_list|,
name|executeQuery
argument_list|(
literal|"/jcr:root//*[@asyncFoo = 'bar']"
argument_list|,
literal|"xpath"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// remove all data
name|builder
operator|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"content"
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|hook
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// run a query
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|executeQuery
argument_list|(
literal|"/jcr:root/content//*[@asyncFoo = 'bar']"
argument_list|,
literal|"xpath"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

