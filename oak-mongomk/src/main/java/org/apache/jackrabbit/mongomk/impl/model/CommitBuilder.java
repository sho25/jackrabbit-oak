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
name|impl
operator|.
name|model
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
name|impl
operator|.
name|instruction
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
name|instruction
operator|.
name|CopyNodeInstructionImpl
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
name|instruction
operator|.
name|MoveNodeInstructionImpl
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
name|instruction
operator|.
name|RemoveNodeInstructionImpl
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
name|instruction
operator|.
name|SetPropertyInstructionImpl
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
name|json
operator|.
name|DefaultJsopHandler
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
name|json
operator|.
name|JsopParser
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
name|util
operator|.
name|MongoUtil
import|;
end_import

begin_comment
comment|/**  * A builder to convert a<a href="http://wiki.apache.org/jackrabbit/Jsop">JSOP</a>  * diff into a {@link Commit}.  */
end_comment

begin_class
specifier|public
class|class
name|CommitBuilder
block|{
comment|/**      * Creates and returns a {@link Commit} without a base revision id.      *      * @param path The root path of the {@code Commit}.      * @param diff The {@code JSOP} diff of the {@code Commit}.      * @param message The message of the {@code Commit}.      *      * @return The {@code Commit}.      * @throws Exception If an error occurred while creating the {@code Commit}.      */
specifier|public
specifier|static
name|Commit
name|build
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|diff
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|CommitBuilder
operator|.
name|build
argument_list|(
name|path
argument_list|,
name|diff
argument_list|,
literal|null
argument_list|,
name|message
argument_list|)
return|;
block|}
comment|/**      * Creates and returns a {@link Commit}.      *      * @param path The root path of the {@code Commit}.      * @param diff The {@code JSOP} diff of the {@code Commit}.      * @param revisionId The revision id the commit is based on.      * @param message The message of the {@code Commit}.      *      * @return The {@code Commit}.      * @throws Exception If an error occurred while creating the {@code Commit}.      */
specifier|public
specifier|static
name|Commit
name|build
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|diff
parameter_list|,
name|String
name|revisionId
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|Exception
block|{
name|MongoCommit
name|commit
init|=
operator|new
name|MongoCommit
argument_list|()
decl_stmt|;
name|commit
operator|.
name|setBaseRevisionId
argument_list|(
name|MongoUtil
operator|.
name|toMongoRepresentation
argument_list|(
name|revisionId
argument_list|)
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setDiff
argument_list|(
name|diff
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|commit
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|JsopParser
name|jsopParser
init|=
operator|new
name|JsopParser
argument_list|(
name|path
argument_list|,
name|diff
argument_list|,
operator|new
name|CommitHandler
argument_list|(
name|commit
argument_list|)
argument_list|)
decl_stmt|;
name|jsopParser
operator|.
name|parse
argument_list|()
expr_stmt|;
return|return
name|commit
return|;
block|}
comment|/**      * The {@link DefaultJaopHandler} for the {@code JSOP} diff.      */
specifier|private
specifier|static
class|class
name|CommitHandler
extends|extends
name|DefaultJsopHandler
block|{
specifier|private
specifier|final
name|MongoCommit
name|commit
decl_stmt|;
name|CommitHandler
parameter_list|(
name|MongoCommit
name|commit
parameter_list|)
block|{
name|this
operator|.
name|commit
operator|=
name|commit
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|nodeAdded
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|commit
operator|.
name|addInstruction
argument_list|(
operator|new
name|AddNodeInstructionImpl
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|nodeCopied
parameter_list|(
name|String
name|rootPath
parameter_list|,
name|String
name|oldPath
parameter_list|,
name|String
name|newPath
parameter_list|)
block|{
name|commit
operator|.
name|addInstruction
argument_list|(
operator|new
name|CopyNodeInstructionImpl
argument_list|(
name|rootPath
argument_list|,
name|oldPath
argument_list|,
name|newPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|nodeMoved
parameter_list|(
name|String
name|rootPath
parameter_list|,
name|String
name|oldPath
parameter_list|,
name|String
name|newPath
parameter_list|)
block|{
name|commit
operator|.
name|addInstruction
argument_list|(
operator|new
name|MoveNodeInstructionImpl
argument_list|(
name|rootPath
argument_list|,
name|oldPath
argument_list|,
name|newPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|nodeRemoved
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|commit
operator|.
name|addInstruction
argument_list|(
operator|new
name|RemoveNodeInstructionImpl
argument_list|(
name|parentPath
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertySet
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|commit
operator|.
name|addInstruction
argument_list|(
operator|new
name|SetPropertyInstructionImpl
argument_list|(
name|path
argument_list|,
name|MongoUtil
operator|.
name|toMongoPropertyKey
argument_list|(
name|key
argument_list|)
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

