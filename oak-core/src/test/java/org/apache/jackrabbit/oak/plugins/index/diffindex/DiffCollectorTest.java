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
name|diffindex
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
name|assertFalse
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
name|Iterator
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
name|PropertyValues
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

begin_class
specifier|public
class|class
name|DiffCollectorTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testUUID
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|EMPTY_NODE
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:uuid"
argument_list|,
literal|"abc"
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
literal|"jcr:uuid"
argument_list|,
literal|"xyz"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|UUIDDiffCollector
name|collector
init|=
operator|new
name|UUIDDiffCollector
argument_list|(
name|root
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|FilterImpl
name|f
init|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"jcr:uuid"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
name|collector
operator|.
name|getResults
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
init|=
name|result
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUUIDInner
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|EMPTY_NODE
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:uuid"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|=
name|before
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
literal|"b"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:uuid"
argument_list|,
literal|"xyz"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|UUIDDiffCollector
name|collector
init|=
operator|new
name|UUIDDiffCollector
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|FilterImpl
name|f
init|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"jcr:uuid"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"xyz"
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
name|collector
operator|.
name|getResults
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
init|=
name|result
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a/b"
argument_list|,
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeepChange
parameter_list|()
throws|throws
name|Exception
block|{
name|NodeState
name|root
init|=
name|EMPTY_NODE
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|root
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|b1
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"rep:security"
argument_list|)
operator|.
name|child
argument_list|(
literal|"rep:authorizables"
argument_list|)
decl_stmt|;
name|b1
operator|.
name|child
argument_list|(
literal|"rep:groups"
argument_list|)
operator|.
name|child
argument_list|(
literal|"t"
argument_list|)
operator|.
name|child
argument_list|(
literal|"te"
argument_list|)
operator|.
name|child
argument_list|(
literal|"testGroup_1c22a39f"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|b2
init|=
name|b1
operator|.
name|child
argument_list|(
literal|"rep:users"
argument_list|)
decl_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"t"
argument_list|)
operator|.
name|child
argument_list|(
literal|"te"
argument_list|)
operator|.
name|child
argument_list|(
literal|"testUser_008e00d9"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|b3
init|=
name|b2
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
decl_stmt|;
name|b3
operator|.
name|child
argument_list|(
literal|"an"
argument_list|)
operator|.
name|child
argument_list|(
literal|"anonymous"
argument_list|)
expr_stmt|;
name|b3
operator|.
name|child
argument_list|(
literal|"ad"
argument_list|)
operator|.
name|child
argument_list|(
literal|"admin"
argument_list|)
expr_stmt|;
name|NodeState
name|before
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|builder
operator|=
name|before
operator|.
name|builder
argument_list|()
expr_stmt|;
name|NodeBuilder
name|a1
init|=
name|builder
operator|.
name|child
argument_list|(
literal|"rep:security"
argument_list|)
operator|.
name|child
argument_list|(
literal|"rep:authorizables"
argument_list|)
operator|.
name|child
argument_list|(
literal|"rep:groups"
argument_list|)
operator|.
name|child
argument_list|(
literal|"t"
argument_list|)
operator|.
name|child
argument_list|(
literal|"te"
argument_list|)
decl_stmt|;
name|a1
operator|.
name|child
argument_list|(
literal|"testGroup_1c22a39f"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:uuid"
argument_list|,
literal|"c6195630-e956-3d4b-8912-479c303bf15a"
argument_list|)
expr_stmt|;
name|a1
operator|.
name|child
argument_list|(
literal|"testPrincipal_4e6b704e"
argument_list|)
operator|.
name|setProperty
argument_list|(
literal|"jcr:uuid"
argument_list|,
literal|"ee59b554-76b7-3e27-9fc6-15bda1388894"
argument_list|)
expr_stmt|;
name|NodeState
name|after
init|=
name|builder
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
name|UUIDDiffCollector
name|collector
init|=
operator|new
name|UUIDDiffCollector
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
decl_stmt|;
name|FilterImpl
name|f
init|=
operator|new
name|FilterImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|f
operator|.
name|restrictProperty
argument_list|(
literal|"jcr:uuid"
argument_list|,
name|Operator
operator|.
name|EQUAL
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
literal|"ee59b554-76b7-3e27-9fc6-15bda1388894"
argument_list|)
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|result
init|=
name|collector
operator|.
name|getResults
argument_list|(
name|f
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
init|=
name|result
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"rep:security/rep:authorizables/rep:groups/t/te/testPrincipal_4e6b704e"
argument_list|,
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|iterator
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

