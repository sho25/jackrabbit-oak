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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Repository
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|api
operator|.
name|JackrabbitRepository
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
name|jcr
operator|.
name|Jcr
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
name|memory
operator|.
name|ArrayBasedBlob
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
name|plugins
operator|.
name|tree
operator|.
name|TreeFactory
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
name|util
operator|.
name|ISO8601
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
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
name|nodetype
operator|.
name|NodeTypeConstants
operator|.
name|NT_OAK_UNSTRUCTURED
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|NodeStateCopyUtilsTest
block|{
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
name|Repository
name|repository
decl_stmt|;
annotation|@
name|After
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
if|if
condition|(
name|repository
operator|instanceof
name|JackrabbitRepository
condition|)
block|{
operator|(
operator|(
name|JackrabbitRepository
operator|)
name|repository
operator|)
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|copyUnordered
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"z"
argument_list|)
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
literal|"c"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"acx"
argument_list|)
expr_stmt|;
name|Tree
name|tree
init|=
name|TreeFactory
operator|.
name|createTree
argument_list|(
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
argument_list|)
decl_stmt|;
name|NodeStateCopyUtils
operator|.
name|copyToTree
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|tree
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|tree
operator|.
name|getProperty
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tree
operator|.
name|getChildrenCount
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"y"
argument_list|,
name|tree
operator|.
name|getChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"z"
argument_list|,
name|tree
operator|.
name|getChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"acx"
argument_list|,
name|tree
operator|.
name|getChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getChild
argument_list|(
literal|"c"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copyOrdered
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeBuilder
name|testBuilder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|Tree
name|srcTree
init|=
name|TreeFactory
operator|.
name|createTree
argument_list|(
name|testBuilder
argument_list|)
decl_stmt|;
name|srcTree
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"z"
argument_list|)
expr_stmt|;
name|Tree
name|tree
init|=
name|TreeFactory
operator|.
name|createTree
argument_list|(
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
argument_list|)
decl_stmt|;
name|NodeStateCopyUtils
operator|.
name|copyToTree
argument_list|(
name|testBuilder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|tree
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|tree
operator|.
name|getProperty
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tree
operator|.
name|getChildrenCount
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"y"
argument_list|,
name|tree
operator|.
name|getChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"z"
argument_list|,
name|tree
operator|.
name|getChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
comment|//Assert the order
name|Iterator
argument_list|<
name|Tree
argument_list|>
name|children
init|=
name|tree
operator|.
name|getChildren
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|children
operator|.
name|next
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|children
operator|.
name|next
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copyToJcr
parameter_list|()
throws|throws
name|Exception
block|{
name|repository
operator|=
operator|new
name|Jcr
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
expr_stmt|;
name|Tree
name|srcTree
init|=
name|TreeFactory
operator|.
name|createTree
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|srcTree
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|addChild
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"z"
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|addChild
argument_list|(
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Node
name|node
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|Node
name|test
init|=
name|node
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|NodeStateCopyUtils
operator|.
name|copyToNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|test
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|test
operator|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/test"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"y"
argument_list|,
name|test
operator|.
name|getProperty
argument_list|(
literal|"a/foo"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"z"
argument_list|,
name|test
operator|.
name|getProperty
argument_list|(
literal|"b/foo"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copyToJcrVariousProps
parameter_list|()
throws|throws
name|Exception
block|{
name|repository
operator|=
operator|new
name|Jcr
argument_list|()
operator|.
name|with
argument_list|(
operator|new
name|OpenSecurityProvider
argument_list|()
argument_list|)
operator|.
name|createRepository
argument_list|()
expr_stmt|;
name|Calendar
name|cal
init|=
name|ISO8601
operator|.
name|parse
argument_list|(
name|ISO8601
operator|.
name|format
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Tree
name|srcTree
init|=
name|TreeFactory
operator|.
name|createTree
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|srcTree
operator|.
name|setOrderableChildren
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|setProperty
argument_list|(
literal|"fooString"
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|setProperty
argument_list|(
literal|"fooLong"
argument_list|,
literal|1L
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|setProperty
argument_list|(
literal|"fooPath"
argument_list|,
literal|"/fooNode"
argument_list|,
name|Type
operator|.
name|PATH
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|setProperty
argument_list|(
literal|"fooName"
argument_list|,
literal|"mix:title"
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|setProperty
argument_list|(
literal|"fooDouble"
argument_list|,
literal|1.0
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|setProperty
argument_list|(
literal|"fooDate"
argument_list|,
name|ISO8601
operator|.
name|format
argument_list|(
name|cal
argument_list|)
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|setProperty
argument_list|(
literal|"fooBoolean"
argument_list|,
literal|true
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|setProperty
argument_list|(
literal|"fooStrings"
argument_list|,
name|asList
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|,
name|Type
operator|.
name|STRINGS
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|setProperty
argument_list|(
literal|"fooBlob"
argument_list|,
operator|new
name|ArrayBasedBlob
argument_list|(
literal|"foo"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
name|Type
operator|.
name|BINARY
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|,
name|Type
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|srcTree
operator|.
name|setProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|,
name|asList
argument_list|(
literal|"mix:mimeType"
argument_list|,
literal|"mix:title"
argument_list|)
argument_list|,
name|Type
operator|.
name|NAMES
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|repository
operator|.
name|login
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Node
name|node
init|=
name|session
operator|.
name|getRootNode
argument_list|()
decl_stmt|;
name|Node
name|test
init|=
name|node
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|Node
name|fooNode
init|=
name|node
operator|.
name|addNode
argument_list|(
literal|"fooNode"
argument_list|,
name|NT_OAK_UNSTRUCTURED
argument_list|)
decl_stmt|;
name|NodeStateCopyUtils
operator|.
name|copyToNode
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|test
argument_list|)
expr_stmt|;
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
name|test
operator|=
name|session
operator|.
name|getNode
argument_list|(
literal|"/test"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"x"
argument_list|,
name|test
operator|.
name|getProperty
argument_list|(
literal|"fooString"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/fooNode"
argument_list|,
name|test
operator|.
name|getProperty
argument_list|(
literal|"fooPath"
argument_list|)
operator|.
name|getNode
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"mix:title"
argument_list|,
name|test
operator|.
name|getProperty
argument_list|(
literal|"fooName"
argument_list|)
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|test
operator|.
name|getProperty
argument_list|(
literal|"fooLong"
argument_list|)
operator|.
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cal
argument_list|,
name|test
operator|.
name|getProperty
argument_list|(
literal|"fooDate"
argument_list|)
operator|.
name|getDate
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|test
operator|.
name|getProperty
argument_list|(
literal|"fooStrings"
argument_list|)
operator|.
name|getValues
argument_list|()
index|[
literal|0
index|]
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|test
operator|.
name|getProperty
argument_list|(
literal|"fooStrings"
argument_list|)
operator|.
name|getValues
argument_list|()
index|[
literal|1
index|]
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
name|test
operator|.
name|getProperty
argument_list|(
literal|"fooBlob"
argument_list|)
operator|.
name|getBinary
argument_list|()
operator|.
name|getStream
argument_list|()
decl_stmt|;
name|String
name|streamVal
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|is
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|streamVal
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

