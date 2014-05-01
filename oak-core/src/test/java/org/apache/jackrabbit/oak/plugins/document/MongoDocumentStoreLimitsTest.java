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
name|document
package|;
end_package

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
name|plugins
operator|.
name|document
operator|.
name|util
operator|.
name|Utils
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
name|EmptyHook
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Strings
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
name|assertNotNull
import|;
end_import

begin_comment
comment|/**  * Test for OAK-1589  */
end_comment

begin_class
specifier|public
class|class
name|MongoDocumentStoreLimitsTest
extends|extends
name|AbstractMongoConnectionTest
block|{
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|longName
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentNodeStore
name|ns
init|=
name|mk
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|ns
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|child
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|ns
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|String
name|longName
init|=
name|Strings
operator|.
name|repeat
argument_list|(
literal|"foo_"
argument_list|,
literal|10000
argument_list|)
decl_stmt|;
name|String
name|longPath
init|=
name|String
operator|.
name|format
argument_list|(
literal|"/test/%s"
argument_list|,
name|longName
argument_list|)
decl_stmt|;
name|builder
operator|=
name|ns
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
literal|"test"
argument_list|)
operator|.
name|child
argument_list|(
name|longName
argument_list|)
expr_stmt|;
try|try
block|{
name|ns
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CommitFailedException
name|e
parameter_list|)
block|{
comment|// expected to fail
return|return;
block|}
comment|// check that the document was created
comment|// when no exception was thrown
name|String
name|id
init|=
name|Utils
operator|.
name|getIdFromPath
argument_list|(
name|longPath
argument_list|)
decl_stmt|;
name|NodeDocument
name|doc
init|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|NODES
argument_list|,
name|id
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

