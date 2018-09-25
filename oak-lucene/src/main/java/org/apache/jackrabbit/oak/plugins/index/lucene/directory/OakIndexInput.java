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
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|directory
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|lucene
operator|.
name|store
operator|.
name|AlreadyClosedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|IndexInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|WeakIdentityMap
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
name|lucene
operator|.
name|directory
operator|.
name|OakIndexFile
operator|.
name|getOakIndexFile
import|;
end_import

begin_class
class|class
name|OakIndexInput
extends|extends
name|IndexInput
block|{
specifier|final
name|OakIndexFile
name|file
decl_stmt|;
specifier|private
name|boolean
name|isClone
init|=
literal|false
decl_stmt|;
specifier|private
specifier|final
name|WeakIdentityMap
argument_list|<
name|OakIndexInput
argument_list|,
name|Boolean
argument_list|>
name|clones
decl_stmt|;
specifier|private
specifier|final
name|String
name|dirDetails
decl_stmt|;
specifier|public
name|OakIndexInput
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeBuilder
name|file
parameter_list|,
name|String
name|dirDetails
parameter_list|,
name|BlobFactory
name|blobFactory
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|dirDetails
operator|=
name|dirDetails
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|getOakIndexFile
argument_list|(
name|name
argument_list|,
name|file
argument_list|,
name|dirDetails
argument_list|,
name|blobFactory
argument_list|)
expr_stmt|;
name|clones
operator|=
name|WeakIdentityMap
operator|.
name|newConcurrentHashMap
argument_list|()
expr_stmt|;
block|}
specifier|private
name|OakIndexInput
parameter_list|(
name|OakIndexInput
name|that
parameter_list|)
block|{
name|super
argument_list|(
name|that
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|that
operator|.
name|file
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clones
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|dirDetails
operator|=
name|that
operator|.
name|dirDetails
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|OakIndexInput
name|clone
parameter_list|()
block|{
comment|// TODO : shouldn't we call super#clone ?
name|OakIndexInput
name|clonedIndexInput
init|=
operator|new
name|OakIndexInput
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|clonedIndexInput
operator|.
name|isClone
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|clones
operator|!=
literal|null
condition|)
block|{
name|clones
operator|.
name|put
argument_list|(
name|clonedIndexInput
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
return|return
name|clonedIndexInput
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|readBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|o
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|checkNotClosed
argument_list|()
expr_stmt|;
name|file
operator|.
name|readBytes
argument_list|(
name|b
argument_list|,
name|o
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|byte
name|readByte
parameter_list|()
throws|throws
name|IOException
block|{
name|checkNotClosed
argument_list|()
expr_stmt|;
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|1
index|]
decl_stmt|;
name|readBytes
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
return|return
name|b
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|seek
parameter_list|(
name|long
name|pos
parameter_list|)
throws|throws
name|IOException
block|{
name|checkNotClosed
argument_list|()
expr_stmt|;
name|file
operator|.
name|seek
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
name|checkNotClosed
argument_list|()
expr_stmt|;
return|return
name|file
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
name|checkNotClosed
argument_list|()
expr_stmt|;
return|return
name|file
operator|.
name|position
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|clones
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|OakIndexInput
argument_list|>
name|it
init|=
name|clones
operator|.
name|keyIterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|OakIndexInput
name|clone
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
assert|assert
name|clone
operator|.
name|isClone
assert|;
name|clone
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|checkNotClosed
parameter_list|()
block|{
if|if
condition|(
name|file
operator|.
name|isClosed
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AlreadyClosedException
argument_list|(
literal|"Already closed: ["
operator|+
name|dirDetails
operator|+
literal|"] "
operator|+
name|this
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

