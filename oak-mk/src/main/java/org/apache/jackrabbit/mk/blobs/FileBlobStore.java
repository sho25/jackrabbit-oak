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
name|blobs
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
name|util
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
name|mk
operator|.
name|util
operator|.
name|StringUtils
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
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|DigestInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|MessageDigest
import|;
end_import

begin_comment
comment|/**  * A file blob store.  */
end_comment

begin_class
specifier|public
class|class
name|FileBlobStore
extends|extends
name|AbstractBlobStore
block|{
specifier|private
specifier|static
specifier|final
name|String
name|OLD_SUFFIX
init|=
literal|"_old"
decl_stmt|;
specifier|private
specifier|final
name|File
name|baseDir
decl_stmt|;
specifier|private
specifier|final
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|16
operator|*
literal|1024
index|]
decl_stmt|;
specifier|private
name|boolean
name|mark
decl_stmt|;
comment|// TODO file operations are not secure (return values not checked, no retry,...)
specifier|public
name|FileBlobStore
parameter_list|(
name|String
name|dir
parameter_list|)
block|{
name|baseDir
operator|=
operator|new
name|File
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|baseDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|writeBlob
parameter_list|(
name|String
name|tempFilePath
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|tempFilePath
argument_list|)
decl_stmt|;
name|InputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|MessageDigest
name|messageDigest
init|=
name|MessageDigest
operator|.
name|getInstance
argument_list|(
name|HASH_ALGORITHM
argument_list|)
decl_stmt|;
name|DigestInputStream
name|din
init|=
operator|new
name|DigestInputStream
argument_list|(
name|in
argument_list|,
name|messageDigest
argument_list|)
decl_stmt|;
name|long
name|length
init|=
name|file
operator|.
name|length
argument_list|()
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|len
init|=
name|din
operator|.
name|read
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|<
literal|0
condition|)
block|{
break|break;
block|}
block|}
block|}
finally|finally
block|{
name|din
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|ByteArrayOutputStream
name|idStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|idStream
operator|.
name|write
argument_list|(
name|TYPE_HASH
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|writeVarInt
argument_list|(
name|idStream
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|writeVarLong
argument_list|(
name|idStream
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|byte
index|[]
name|digest
init|=
name|messageDigest
operator|.
name|digest
argument_list|()
decl_stmt|;
name|File
name|f
init|=
name|getFile
argument_list|(
name|digest
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|File
name|parent
init|=
name|f
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|parent
operator|.
name|exists
argument_list|()
condition|)
block|{
name|parent
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|file
operator|.
name|renameTo
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
name|IOUtils
operator|.
name|writeVarInt
argument_list|(
name|idStream
argument_list|,
name|digest
operator|.
name|length
argument_list|)
expr_stmt|;
name|idStream
operator|.
name|write
argument_list|(
name|digest
argument_list|)
expr_stmt|;
name|byte
index|[]
name|id
init|=
name|idStream
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|String
name|blobId
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|usesBlobId
argument_list|(
name|blobId
argument_list|)
expr_stmt|;
return|return
name|blobId
return|;
block|}
annotation|@
name|Override
specifier|protected
specifier|synchronized
name|void
name|storeBlock
parameter_list|(
name|byte
index|[]
name|digest
parameter_list|,
name|int
name|level
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|f
init|=
name|getFile
argument_list|(
name|digest
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return;
block|}
name|File
name|parent
init|=
name|f
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|parent
operator|.
name|exists
argument_list|()
condition|)
block|{
name|parent
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|File
name|temp
init|=
operator|new
name|File
argument_list|(
name|parent
argument_list|,
name|f
operator|.
name|getName
argument_list|()
operator|+
literal|".temp"
argument_list|)
decl_stmt|;
name|OutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|temp
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|temp
operator|.
name|renameTo
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
specifier|private
name|File
name|getFile
parameter_list|(
name|byte
index|[]
name|digest
parameter_list|,
name|boolean
name|old
parameter_list|)
block|{
name|String
name|id
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|digest
argument_list|)
decl_stmt|;
name|String
name|sub1
init|=
name|id
operator|.
name|substring
argument_list|(
name|id
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
decl_stmt|;
name|String
name|sub2
init|=
name|id
operator|.
name|substring
argument_list|(
name|id
operator|.
name|length
argument_list|()
operator|-
literal|4
argument_list|,
name|id
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
condition|)
block|{
name|sub2
operator|+=
name|OLD_SUFFIX
expr_stmt|;
block|}
return|return
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|sub1
argument_list|)
argument_list|,
name|sub2
argument_list|)
argument_list|,
name|id
operator|+
literal|".dat"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|byte
index|[]
name|readBlockFromBackend
parameter_list|(
name|BlockId
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|f
init|=
name|getFile
argument_list|(
name|id
operator|.
name|digest
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
name|File
name|old
init|=
name|getFile
argument_list|(
name|id
operator|.
name|digest
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|f
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|old
operator|.
name|renameTo
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|f
operator|=
name|getFile
argument_list|(
name|id
operator|.
name|digest
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|int
name|length
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|min
argument_list|(
name|f
operator|.
name|length
argument_list|()
argument_list|,
name|getBlockSize
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|InputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|f
argument_list|)
decl_stmt|;
try|try
block|{
name|IOUtils
operator|.
name|skipFully
argument_list|(
name|in
argument_list|,
name|id
operator|.
name|pos
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|readFully
argument_list|(
name|in
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startMark
parameter_list|()
throws|throws
name|Exception
block|{
name|mark
operator|=
literal|true
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|256
condition|;
name|j
operator|++
control|)
block|{
name|String
name|sub1
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
name|j
block|}
argument_list|)
decl_stmt|;
name|File
name|x
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|sub1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|256
condition|;
name|i
operator|++
control|)
block|{
name|String
name|sub2
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
name|i
block|}
argument_list|)
decl_stmt|;
name|File
name|d
init|=
operator|new
name|File
argument_list|(
name|x
argument_list|,
name|sub2
argument_list|)
decl_stmt|;
name|File
name|old
init|=
operator|new
name|File
argument_list|(
name|x
argument_list|,
name|sub2
operator|+
name|OLD_SUFFIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|old
operator|.
name|exists
argument_list|()
condition|)
block|{
for|for
control|(
name|File
name|p
range|:
name|d
operator|.
name|listFiles
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|p
operator|.
name|getName
argument_list|()
decl_stmt|;
name|File
name|newName
init|=
operator|new
name|File
argument_list|(
name|old
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|p
operator|.
name|renameTo
argument_list|(
name|newName
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|d
operator|.
name|renameTo
argument_list|(
name|old
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|markInUse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isMarkEnabled
parameter_list|()
block|{
return|return
name|mark
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|mark
parameter_list|(
name|BlockId
name|id
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|f
init|=
name|getFile
argument_list|(
name|id
operator|.
name|digest
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
name|File
name|old
init|=
name|getFile
argument_list|(
name|id
operator|.
name|digest
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|f
operator|.
name|getParentFile
argument_list|()
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|old
operator|.
name|renameTo
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|f
operator|=
name|getFile
argument_list|(
name|id
operator|.
name|digest
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|sweep
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|256
condition|;
name|j
operator|++
control|)
block|{
name|String
name|sub1
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
name|j
block|}
argument_list|)
decl_stmt|;
name|File
name|x
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|sub1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|256
condition|;
name|i
operator|++
control|)
block|{
name|String
name|sub
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
name|i
block|}
argument_list|)
decl_stmt|;
name|File
name|old
init|=
operator|new
name|File
argument_list|(
name|x
argument_list|,
name|sub
operator|+
name|OLD_SUFFIX
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|.
name|exists
argument_list|()
condition|)
block|{
for|for
control|(
name|File
name|p
range|:
name|old
operator|.
name|listFiles
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|p
operator|.
name|getName
argument_list|()
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|old
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
name|old
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|mark
operator|=
literal|false
expr_stmt|;
return|return
name|count
return|;
block|}
block|}
end_class

end_unit

