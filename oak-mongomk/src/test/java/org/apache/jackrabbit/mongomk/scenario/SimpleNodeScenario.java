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
name|mongomk
operator|.
name|scenario
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mongomk
operator|.
name|api
operator|.
name|model
operator|.
name|Commit
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
name|mongomk
operator|.
name|api
operator|.
name|model
operator|.
name|Instruction
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
name|mongomk
operator|.
name|command
operator|.
name|CommitCommandMongo
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
name|mongomk
operator|.
name|impl
operator|.
name|MongoConnection
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
name|mongomk
operator|.
name|impl
operator|.
name|builder
operator|.
name|CommitBuilder
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
name|mongomk
operator|.
name|impl
operator|.
name|model
operator|.
name|AddNodeInstructionImpl
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
name|mongomk
operator|.
name|impl
operator|.
name|model
operator|.
name|AddPropertyInstructionImpl
import|;
end_import

begin_comment
comment|/**  * Creates a defined scenario in {@code MongoDB}.  *  * @author<a href="mailto:pmarx@adobe.com>Philipp Marx</a>  */
end_comment

begin_class
specifier|public
class|class
name|SimpleNodeScenario
block|{
specifier|private
specifier|final
name|MongoConnection
name|mongoConnection
decl_stmt|;
comment|/**      * Constructs a new {@code SimpleNodeScenario}.      *      * @param mongoConnection The {@link MongoConnection}.      */
specifier|public
name|SimpleNodeScenario
parameter_list|(
name|MongoConnection
name|mongoConnection
parameter_list|)
block|{
name|this
operator|.
name|mongoConnection
operator|=
name|mongoConnection
expr_stmt|;
block|}
comment|/**      * Creates the following nodes:      *      *<pre>      *&quot;+a : { \&quot;int\&quot; : 1 , \&quot;b\&quot; : { \&quot;string\&quot; : \&quot;foo\&quot; } , \&quot;c\&quot; : { \&quot;bool\&quot; : true } } }&quot;      *</pre>      *      * @return The {@link RevisionId}.      * @throws Exception If an error occurred.      */
specifier|public
name|Long
name|create
parameter_list|()
throws|throws
name|Exception
block|{
name|Commit
name|commit
init|=
name|CommitBuilder
operator|.
name|build
argument_list|(
literal|"/"
argument_list|,
literal|"+\"a\" : { \"int\" : 1 , \"b\" : { \"string\" : \"foo\" } , \"c\" : { \"bool\" : true } }"
argument_list|,
literal|"This is the simple node scenario with nodes /, /a, /a/b, /a/c"
argument_list|)
decl_stmt|;
name|CommitCommandMongo
name|command
init|=
operator|new
name|CommitCommandMongo
argument_list|(
name|mongoConnection
argument_list|,
name|commit
argument_list|)
decl_stmt|;
return|return
name|command
operator|.
name|execute
argument_list|()
return|;
block|}
specifier|public
name|Long
name|addChildrenToA
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|Long
name|revisionId
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|count
condition|;
name|i
operator|++
control|)
block|{
name|Commit
name|commit
init|=
name|CommitBuilder
operator|.
name|build
argument_list|(
literal|"/a"
argument_list|,
literal|"+\"child"
operator|+
name|i
operator|+
literal|"\" : {}"
argument_list|,
literal|"Add child"
operator|+
name|i
argument_list|)
decl_stmt|;
name|CommitCommandMongo
name|command
init|=
operator|new
name|CommitCommandMongo
argument_list|(
name|mongoConnection
argument_list|,
name|commit
argument_list|)
decl_stmt|;
name|revisionId
operator|=
name|command
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
return|return
name|revisionId
return|;
block|}
specifier|public
name|Long
name|delete_A
parameter_list|()
throws|throws
name|Exception
block|{
name|Commit
name|commit
init|=
name|CommitBuilder
operator|.
name|build
argument_list|(
literal|"/"
argument_list|,
literal|"-\"a\""
argument_list|,
literal|"This is a commit with deleted /a"
argument_list|)
decl_stmt|;
name|CommitCommandMongo
name|command
init|=
operator|new
name|CommitCommandMongo
argument_list|(
name|mongoConnection
argument_list|,
name|commit
argument_list|)
decl_stmt|;
return|return
name|command
operator|.
name|execute
argument_list|()
return|;
block|}
specifier|public
name|Long
name|delete_B
parameter_list|()
throws|throws
name|Exception
block|{
name|Commit
name|commit
init|=
name|CommitBuilder
operator|.
name|build
argument_list|(
literal|"/a"
argument_list|,
literal|"-\"b\""
argument_list|,
literal|"This is a commit with deleted /a/b"
argument_list|)
decl_stmt|;
name|CommitCommandMongo
name|command
init|=
operator|new
name|CommitCommandMongo
argument_list|(
name|mongoConnection
argument_list|,
name|commit
argument_list|)
decl_stmt|;
return|return
name|command
operator|.
name|execute
argument_list|()
return|;
block|}
specifier|public
name|Long
name|update_A_and_add_D_and_E
parameter_list|()
throws|throws
name|Exception
block|{
name|List
argument_list|<
name|Instruction
argument_list|>
name|instructions
init|=
operator|new
name|LinkedList
argument_list|<
name|Instruction
argument_list|>
argument_list|()
decl_stmt|;
name|instructions
operator|.
name|add
argument_list|(
operator|new
name|AddNodeInstructionImpl
argument_list|(
literal|"/a"
argument_list|,
literal|"d"
argument_list|)
argument_list|)
expr_stmt|;
name|instructions
operator|.
name|add
argument_list|(
operator|new
name|AddNodeInstructionImpl
argument_list|(
literal|"/a/b"
argument_list|,
literal|"e"
argument_list|)
argument_list|)
expr_stmt|;
name|instructions
operator|.
name|add
argument_list|(
operator|new
name|AddPropertyInstructionImpl
argument_list|(
literal|"/a"
argument_list|,
literal|"double"
argument_list|,
literal|0.123D
argument_list|)
argument_list|)
expr_stmt|;
name|instructions
operator|.
name|add
argument_list|(
operator|new
name|AddPropertyInstructionImpl
argument_list|(
literal|"/a/d"
argument_list|,
literal|"null"
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|instructions
operator|.
name|add
argument_list|(
operator|new
name|AddPropertyInstructionImpl
argument_list|(
literal|"/a/b/e"
argument_list|,
literal|"array"
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|123
block|,
literal|null
block|,
literal|123.456D
block|,
literal|"for:bar"
block|,
name|Boolean
operator|.
name|TRUE
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|StringBuilder
name|diff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|diff
operator|.
name|append
argument_list|(
literal|"+\"a/d\" : {}"
argument_list|)
expr_stmt|;
name|diff
operator|.
name|append
argument_list|(
literal|"+\"a/b/e\" : {}"
argument_list|)
expr_stmt|;
name|diff
operator|.
name|append
argument_list|(
literal|"+\"a/double\" : 0.123"
argument_list|)
expr_stmt|;
name|diff
operator|.
name|append
argument_list|(
literal|"+\"a/d/null\" :  null"
argument_list|)
expr_stmt|;
name|diff
operator|.
name|append
argument_list|(
literal|"+\"a/b/e/array\" : [ 123, null, 123.456, \"for:bar\", true ]"
argument_list|)
expr_stmt|;
name|Commit
name|commit
init|=
name|CommitBuilder
operator|.
name|build
argument_list|(
literal|"/"
argument_list|,
name|diff
operator|.
name|toString
argument_list|()
argument_list|,
literal|"This is a commit with updated /a and added /a/d and /a/b/e"
argument_list|)
decl_stmt|;
name|CommitCommandMongo
name|command
init|=
operator|new
name|CommitCommandMongo
argument_list|(
name|mongoConnection
argument_list|,
name|commit
argument_list|)
decl_stmt|;
return|return
name|command
operator|.
name|execute
argument_list|()
return|;
block|}
block|}
end_class

end_unit

