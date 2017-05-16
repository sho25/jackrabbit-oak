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
name|plugins
operator|.
name|index
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
name|ImmutableList
operator|.
name|of
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
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|NT_UNSTRUCTURED
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Oak
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
name|ContentRepository
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
name|InitialContent
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
name|AbstractQueryTest
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
name|OpenSecurityProvider
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
name|util
operator|.
name|NodeUtil
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

begin_comment
comment|/**  * Tests the node type index implementation.  */
end_comment

begin_class
specifier|public
class|class
name|NodeTypeIndexQueryTest
extends|extends
name|AbstractQueryTest
block|{
annotation|@
name|Override
specifier|protected
name|ContentRepository
name|createRepository
parameter_list|()
block|{
return|return
operator|new
name|Oak
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|InitialContent
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|NodeTypeIndexProvider
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
operator|new
name|PropertyIndexEditorProvider
argument_list|()
argument_list|)
operator|.
name|createContentRepository
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|Tree
name|child
parameter_list|(
name|Tree
name|t
parameter_list|,
name|String
name|n
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|Tree
name|t1
init|=
name|t
operator|.
name|addChild
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|t1
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|type
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
return|return
name|t1
return|;
block|}
specifier|private
specifier|static
name|void
name|mixLanguage
parameter_list|(
name|Tree
name|t
parameter_list|,
name|String
name|n
parameter_list|)
block|{
name|Tree
name|c
init|=
name|t
operator|.
name|addChild
argument_list|(
name|n
argument_list|)
decl_stmt|;
name|c
operator|.
name|setProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
literal|"nt:unstructured"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|c
operator|.
name|setProperty
argument_list|(
name|JCR_MIXINTYPES
argument_list|,
name|of
argument_list|(
literal|"mix:language"
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|query
parameter_list|()
throws|throws
name|Exception
block|{
name|setTraversalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Tree
name|t
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|child
argument_list|(
name|t
argument_list|,
literal|"a"
argument_list|,
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|t
argument_list|,
literal|"b"
argument_list|,
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|t
argument_list|,
literal|"c"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|t
argument_list|,
literal|"d"
argument_list|,
literal|"nt:folder"
argument_list|)
expr_stmt|;
name|mixLanguage
argument_list|(
name|t
argument_list|,
literal|"e"
argument_list|)
expr_stmt|;
name|mixLanguage
argument_list|(
name|t
argument_list|,
literal|"f"
argument_list|)
expr_stmt|;
name|Tree
name|n
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/oak:index"
argument_list|)
decl_stmt|;
name|createIndexDefinition
argument_list|(
name|n
argument_list|,
literal|"nodetype"
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[]
block|{
name|JCR_PRIMARYTYPE
block|,
name|JCR_MIXINTYPES
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"nt:folder"
block|,
literal|"mix:language"
block|}
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [nt:unstructured] "
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [nt:folder] "
argument_list|,
name|of
argument_list|(
literal|"/c"
argument_list|,
literal|"/d"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|"select [jcr:path] from [mix:language] "
argument_list|,
name|of
argument_list|(
literal|"/e"
argument_list|,
literal|"/f"
argument_list|)
argument_list|)
expr_stmt|;
name|setTraversalEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|oak3371
parameter_list|()
throws|throws
name|Exception
block|{
name|setTraversalEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Tree
name|t
decl_stmt|,
name|t1
decl_stmt|;
name|Tree
name|n
init|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/oak:index"
argument_list|)
decl_stmt|;
name|createIndexDefinition
argument_list|(
name|n
argument_list|,
literal|"nodeType"
argument_list|,
literal|false
argument_list|,
operator|new
name|String
index|[]
block|{
name|JCR_PRIMARYTYPE
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
name|NT_UNSTRUCTURED
block|}
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|t
operator|=
name|root
operator|.
name|getTree
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
name|t
operator|=
name|child
argument_list|(
name|t
argument_list|,
literal|"test"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|t1
operator|=
name|child
argument_list|(
name|t
argument_list|,
literal|"a"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|t1
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|child
argument_list|(
name|t
argument_list|,
literal|"b"
argument_list|,
name|NT_UNSTRUCTURED
argument_list|)
expr_stmt|;
name|root
operator|.
name|commit
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|plan
decl_stmt|;
name|plan
operator|=
name|executeQuery
argument_list|(
literal|"explain SELECT * FROM [nt:unstructured] "
operator|+
literal|"WHERE ISDESCENDANTNODE([/test]) "
operator|+
literal|"AND CONTAINS(foo, 'bar')"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|plan
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|plan
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|contains
argument_list|(
literal|"no-index"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[nt:unstructured] as [nt:unstructured] /* no-index\n"
operator|+
literal|"  where (isdescendantnode([nt:unstructured], [/test]))\n"
operator|+
literal|"  and (contains([nt:unstructured].[foo], 'bar')) */"
argument_list|,
name|plan
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|plan
operator|=
name|executeQuery
argument_list|(
literal|"explain SELECT * FROM [nt:unstructured] "
operator|+
literal|"WHERE ISDESCENDANTNODE([/test]) "
operator|+
literal|"AND NOT CONTAINS(foo, 'bar')"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|plan
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|plan
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|contains
argument_list|(
literal|"no-index"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[nt:unstructured] as [nt:unstructured] /* no-index\n"
operator|+
literal|"  where (isdescendantnode([nt:unstructured], [/test]))\n"
operator|+
literal|"  and (not contains([nt:unstructured].[foo], 'bar')) */"
argument_list|,
name|plan
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|plan
operator|=
name|executeQuery
argument_list|(
literal|"explain SELECT * FROM [nt:unstructured] "
operator|+
literal|"WHERE ISDESCENDANTNODE([/test]) "
operator|+
literal|"AND NOT NOT CONTAINS(foo, 'bar')"
argument_list|,
name|Query
operator|.
name|JCR_SQL2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|plan
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|plan
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|contains
argument_list|(
literal|"no-index"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[nt:unstructured] as [nt:unstructured] /* no-index\n"
operator|+
literal|"  where (isdescendantnode([nt:unstructured], [/test]))\n"
operator|+
literal|"  and (contains([nt:unstructured].[foo], 'bar')) */"
argument_list|,
name|plan
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|setTraversalEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

