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
name|lucene
package|;
end_package

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
name|IOException
import|;
end_import

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
name|Collection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
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
name|CoreValue
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
name|CoreValueFactory
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
name|PropertyState
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
name|NodeStore
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
name|Directory
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
name|IOContext
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
name|store
operator|.
name|IndexOutput
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
name|NoLockFactory
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
name|Iterables
import|;
end_import

begin_comment
comment|/**  * An implementation of the Lucene directory (a flat list of files) that allows  * to store Lucene index content in an Oak repository.  */
end_comment

begin_class
class|class
name|OakDirectory
extends|extends
name|Directory
block|{
specifier|private
specifier|final
name|CoreValueFactory
name|factory
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|rootBuilder
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|directoryBuilder
decl_stmt|;
specifier|public
name|OakDirectory
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|NodeState
name|root
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
block|{
name|this
operator|.
name|lockFactory
operator|=
name|NoLockFactory
operator|.
name|getNoLockFactory
argument_list|()
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|store
operator|.
name|getValueFactory
argument_list|()
expr_stmt|;
name|this
operator|.
name|rootBuilder
operator|=
name|store
operator|.
name|getBuilder
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|NodeBuilder
name|builder
init|=
name|rootBuilder
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|path
control|)
block|{
name|builder
operator|=
name|builder
operator|.
name|getChildBuilder
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|directoryBuilder
operator|=
name|builder
expr_stmt|;
block|}
annotation|@
name|Nonnull
name|NodeState
name|getRoot
parameter_list|()
block|{
return|return
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|listAll
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Iterables
operator|.
name|toArray
argument_list|(
name|directoryBuilder
operator|.
name|getChildNodeNames
argument_list|()
argument_list|,
name|String
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|fileExists
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|directoryBuilder
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deleteFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|directoryBuilder
operator|.
name|removeNode
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|fileLength
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|fileExists
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
name|NodeBuilder
name|fileBuilder
init|=
name|directoryBuilder
operator|.
name|getChildBuilder
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|PropertyState
name|property
init|=
name|fileBuilder
operator|.
name|getProperty
argument_list|(
literal|"jcr:data"
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|==
literal|null
operator|||
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|property
operator|.
name|getValue
argument_list|()
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexOutput
name|createOutput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|OakIndexOutput
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexInput
name|openInput
parameter_list|(
name|String
name|name
parameter_list|,
name|IOContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|OakIndexInput
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|sync
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
throws|throws
name|IOException
block|{
comment|// ?
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
comment|// do nothing
block|}
specifier|private
name|byte
index|[]
name|readFile
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|fileExists
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
operator|new
name|byte
index|[
literal|0
index|]
return|;
block|}
name|NodeBuilder
name|fileBuilder
init|=
name|directoryBuilder
operator|.
name|getChildBuilder
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|PropertyState
name|property
init|=
name|fileBuilder
operator|.
name|getProperty
argument_list|(
literal|"jcr:data"
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|==
literal|null
operator|||
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
operator|new
name|byte
index|[
literal|0
index|]
return|;
block|}
name|CoreValue
name|value
init|=
name|property
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|InputStream
name|stream
init|=
name|value
operator|.
name|getNewStream
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|value
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|int
name|size
init|=
literal|0
decl_stmt|;
do|do
block|{
name|int
name|n
init|=
name|stream
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
name|size
argument_list|,
name|buffer
operator|.
name|length
operator|-
name|size
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unexpected end of index file: "
operator|+
name|name
argument_list|)
throw|;
block|}
name|size
operator|+=
name|n
expr_stmt|;
block|}
do|while
condition|(
name|size
operator|<
name|buffer
operator|.
name|length
condition|)
do|;
return|return
name|buffer
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
class|class
name|OakIndexOutput
extends|extends
name|IndexOutput
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|byte
index|[]
name|buffer
decl_stmt|;
specifier|private
name|int
name|size
decl_stmt|;
specifier|private
name|int
name|position
decl_stmt|;
specifier|public
name|OakIndexOutput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|buffer
operator|=
name|readFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|buffer
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|position
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|position
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
if|if
condition|(
name|pos
argument_list|<
literal|0
operator|||
name|pos
argument_list|>
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid file position: "
operator|+
name|pos
argument_list|)
throw|;
block|}
name|this
operator|.
name|position
operator|=
operator|(
name|int
operator|)
name|pos
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeBytes
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
while|while
condition|(
name|position
operator|+
name|length
operator|>
name|buffer
operator|.
name|length
condition|)
block|{
name|byte
index|[]
name|tmp
init|=
operator|new
name|byte
index|[
name|Math
operator|.
name|max
argument_list|(
literal|4096
argument_list|,
name|buffer
operator|.
name|length
operator|*
literal|2
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|tmp
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
name|buffer
operator|=
name|tmp
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|b
argument_list|,
name|offset
argument_list|,
name|buffer
argument_list|,
name|position
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|position
operator|+=
name|length
expr_stmt|;
if|if
condition|(
name|position
operator|>
name|size
condition|)
block|{
name|size
operator|=
name|position
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeByte
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
name|writeBytes
argument_list|(
operator|new
name|byte
index|[]
block|{
name|b
block|}
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
name|buffer
decl_stmt|;
if|if
condition|(
name|data
operator|.
name|length
operator|>
name|size
condition|)
block|{
name|data
operator|=
operator|new
name|byte
index|[
name|size
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
name|NodeBuilder
name|fileBuilder
init|=
name|directoryBuilder
operator|.
name|getChildBuilder
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|fileBuilder
operator|.
name|setProperty
argument_list|(
literal|"jcr:lastModified"
argument_list|,
name|factory
operator|.
name|createValue
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|fileBuilder
operator|.
name|setProperty
argument_list|(
literal|"jcr:data"
argument_list|,
name|factory
operator|.
name|createValue
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|data
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
class|class
name|OakIndexInput
extends|extends
name|IndexInput
block|{
specifier|private
specifier|final
name|byte
index|[]
name|data
decl_stmt|;
specifier|private
name|int
name|position
decl_stmt|;
specifier|public
name|OakIndexInput
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|readFile
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|position
operator|=
literal|0
expr_stmt|;
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
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|len
argument_list|<
literal|0
operator|||
name|position
operator|+
name|len
argument_list|>
name|data
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid byte range request"
argument_list|)
throw|;
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|position
argument_list|,
name|b
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|position
operator|+=
name|len
expr_stmt|;
block|}
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
if|if
condition|(
name|position
operator|>=
name|data
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid byte range request"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
name|data
index|[
name|position
operator|++
index|]
return|;
block|}
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
if|if
condition|(
name|pos
operator|<
literal|0
operator|||
name|pos
operator|>=
name|data
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Invalid seek request"
argument_list|)
throw|;
block|}
else|else
block|{
name|position
operator|=
operator|(
name|int
operator|)
name|pos
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|data
operator|.
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getFilePointer
parameter_list|()
block|{
return|return
name|position
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// do nothing
block|}
block|}
block|}
end_class

end_unit

