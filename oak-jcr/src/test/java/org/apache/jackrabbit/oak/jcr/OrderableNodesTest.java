begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
package|;
end_package

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
name|NodeIterator
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
name|Session
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_class
specifier|public
class|class
name|OrderableNodesTest
extends|extends
name|AbstractRepositoryTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testSimpleOrdering
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|doTest
argument_list|(
literal|"nt:unstructured"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|orderableFolder
parameter_list|()
throws|throws
name|Exception
block|{
comment|// check ordering with node type without a residual properties definition
operator|new
name|TestContentLoader
argument_list|()
operator|.
name|loadTestContent
argument_list|(
name|getAdminSession
argument_list|()
argument_list|)
expr_stmt|;
name|doTest
argument_list|(
literal|"test:orderableFolder"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doTest
parameter_list|(
name|String
name|nodeType
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|Session
name|session
init|=
name|getAdminSession
argument_list|()
decl_stmt|;
name|Node
name|root
init|=
name|session
operator|.
name|getRootNode
argument_list|()
operator|.
name|addNode
argument_list|(
literal|"test"
argument_list|,
name|nodeType
argument_list|)
decl_stmt|;
name|root
operator|.
name|addNode
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|root
operator|.
name|addNode
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|root
operator|.
name|addNode
argument_list|(
literal|"c"
argument_list|)
expr_stmt|;
name|NodeIterator
name|iterator
decl_stmt|;
name|root
operator|.
name|orderBefore
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
expr_stmt|;
name|root
operator|.
name|orderBefore
argument_list|(
literal|"c"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|root
operator|.
name|getNodes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|iterator
operator|.
name|nextNode
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
name|iterator
operator|.
name|nextNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"c"
argument_list|,
name|iterator
operator|.
name|nextNode
argument_list|()
operator|.
name|getName
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
name|root
operator|.
name|orderBefore
argument_list|(
literal|"c"
argument_list|,
literal|"a"
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|root
operator|.
name|getNodes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"c"
argument_list|,
name|iterator
operator|.
name|nextNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|iterator
operator|.
name|nextNode
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
name|iterator
operator|.
name|nextNode
argument_list|()
operator|.
name|getName
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
name|root
operator|.
name|orderBefore
argument_list|(
literal|"b"
argument_list|,
literal|"c"
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|root
operator|.
name|getNodes
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"b"
argument_list|,
name|iterator
operator|.
name|nextNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"c"
argument_list|,
name|iterator
operator|.
name|nextNode
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|iterator
operator|.
name|nextNode
argument_list|()
operator|.
name|getName
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
name|session
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

