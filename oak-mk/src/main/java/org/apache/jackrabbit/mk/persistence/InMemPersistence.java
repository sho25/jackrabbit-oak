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
name|mk
operator|.
name|persistence
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
name|mk
operator|.
name|model
operator|.
name|ChildNodeEntries
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
name|mk
operator|.
name|model
operator|.
name|ChildNodeEntriesMap
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
name|mk
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
name|mk
operator|.
name|model
operator|.
name|Id
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
name|mk
operator|.
name|model
operator|.
name|Node
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
name|mk
operator|.
name|model
operator|.
name|StoredCommit
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
name|mk
operator|.
name|model
operator|.
name|StoredNode
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
name|mk
operator|.
name|store
operator|.
name|BinaryBinding
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
name|mk
operator|.
name|store
operator|.
name|IdFactory
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
name|mk
operator|.
name|store
operator|.
name|NotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|InMemPersistence
implements|implements
name|GCPersistence
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|Id
argument_list|,
name|byte
index|[]
argument_list|>
name|objects
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|Id
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|Id
argument_list|,
name|byte
index|[]
argument_list|>
name|marked
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|Id
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|Id
name|head
decl_stmt|;
specifier|private
name|long
name|gcStart
decl_stmt|;
comment|// TODO: make this configurable
specifier|private
name|IdFactory
name|idFactory
init|=
name|IdFactory
operator|.
name|getDigestFactory
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|File
name|homeDir
parameter_list|)
block|{
comment|// nothing to initialize
block|}
specifier|public
name|Id
name|readHead
parameter_list|()
block|{
return|return
name|head
return|;
block|}
specifier|public
name|void
name|writeHead
parameter_list|(
name|Id
name|id
parameter_list|)
block|{
name|head
operator|=
name|id
expr_stmt|;
block|}
specifier|public
name|void
name|readNode
parameter_list|(
name|StoredNode
name|node
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|Id
name|id
init|=
name|node
operator|.
name|getId
argument_list|()
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|objects
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
name|node
operator|.
name|deserialize
argument_list|(
operator|new
name|BinaryBinding
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
specifier|public
name|Id
name|writeNode
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|node
operator|.
name|serialize
argument_list|(
operator|new
name|BinaryBinding
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|Id
name|id
init|=
operator|new
name|Id
argument_list|(
name|idFactory
operator|.
name|createContentId
argument_list|(
name|bytes
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|objects
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|objects
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|gcStart
operator|!=
literal|0
condition|)
block|{
name|marked
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
specifier|public
name|StoredCommit
name|readCommit
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|byte
index|[]
name|bytes
init|=
name|objects
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
return|return
name|StoredCommit
operator|.
name|deserialize
argument_list|(
name|id
argument_list|,
operator|new
name|BinaryBinding
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
specifier|public
name|void
name|writeCommit
parameter_list|(
name|Id
name|id
parameter_list|,
name|Commit
name|commit
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|commit
operator|.
name|serialize
argument_list|(
operator|new
name|BinaryBinding
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|objects
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|objects
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|gcStart
operator|!=
literal|0
condition|)
block|{
name|marked
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|ChildNodeEntriesMap
name|readCNEMap
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
throws|,
name|Exception
block|{
name|byte
index|[]
name|bytes
init|=
name|objects
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
return|return
name|ChildNodeEntriesMap
operator|.
name|deserialize
argument_list|(
operator|new
name|BinaryBinding
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bytes
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
specifier|public
name|Id
name|writeCNEMap
parameter_list|(
name|ChildNodeEntries
name|map
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|map
operator|.
name|serialize
argument_list|(
operator|new
name|BinaryBinding
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|Id
name|id
init|=
operator|new
name|Id
argument_list|(
name|idFactory
operator|.
name|createContentId
argument_list|(
name|bytes
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|objects
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|objects
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|gcStart
operator|!=
literal|0
condition|)
block|{
name|marked
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// nothing to do here
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|()
block|{
name|gcStart
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|marked
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|markCommit
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
block|{
return|return
name|markObject
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|markNode
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
block|{
return|return
name|markObject
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|markCNEMap
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
block|{
return|return
name|markObject
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|replaceCommit
parameter_list|(
name|Id
name|id
parameter_list|,
name|Commit
name|commit
parameter_list|)
throws|throws
name|Exception
block|{
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|commit
operator|.
name|serialize
argument_list|(
operator|new
name|BinaryBinding
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|objects
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
name|marked
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|markObject
parameter_list|(
name|Id
name|id
parameter_list|)
throws|throws
name|NotFoundException
block|{
name|byte
index|[]
name|data
init|=
name|objects
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
return|return
name|marked
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|data
argument_list|)
operator|==
literal|null
return|;
block|}
throw|throw
operator|new
name|NotFoundException
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|sweep
parameter_list|()
block|{
name|int
name|count
init|=
name|objects
operator|.
name|size
argument_list|()
decl_stmt|;
name|objects
operator|.
name|clear
argument_list|()
expr_stmt|;
name|objects
operator|.
name|putAll
argument_list|(
name|marked
argument_list|)
expr_stmt|;
name|gcStart
operator|=
literal|0
expr_stmt|;
return|return
name|count
return|;
block|}
block|}
end_class

end_unit

