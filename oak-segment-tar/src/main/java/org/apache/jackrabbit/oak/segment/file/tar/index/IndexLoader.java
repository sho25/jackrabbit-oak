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
name|segment
operator|.
name|file
operator|.
name|tar
operator|.
name|index
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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

begin_comment
comment|/**  * Load and validate the index of a TAR file.  */
end_comment

begin_class
specifier|public
class|class
name|IndexLoader
block|{
comment|/**      * Create a new {@link IndexLoader} for the specified block size. The block      * size is used to validate different data items in the index.      *      * @param blockSize The block size. It must be strictly positive.      * @return An instance of {@link IndexLoader}.      */
specifier|public
specifier|static
name|IndexLoader
name|newIndexLoader
parameter_list|(
name|int
name|blockSize
parameter_list|)
block|{
name|checkArgument
argument_list|(
name|blockSize
operator|>
literal|0
argument_list|,
literal|"Invalid block size"
argument_list|)
expr_stmt|;
return|return
operator|new
name|IndexLoader
argument_list|(
name|blockSize
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|IndexLoaderV1
name|v1
decl_stmt|;
specifier|private
specifier|final
name|IndexLoaderV2
name|v2
decl_stmt|;
specifier|private
name|IndexLoader
parameter_list|(
name|int
name|blockSize
parameter_list|)
block|{
name|this
operator|.
name|v1
operator|=
operator|new
name|IndexLoaderV1
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
name|this
operator|.
name|v2
operator|=
operator|new
name|IndexLoaderV2
argument_list|(
name|blockSize
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|readMagic
parameter_list|(
name|ReaderAtEnd
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|reader
operator|.
name|readAtEnd
argument_list|(
name|Integer
operator|.
name|BYTES
argument_list|,
name|Integer
operator|.
name|BYTES
argument_list|)
operator|.
name|getInt
argument_list|()
return|;
block|}
comment|/**      * Load and validate the index. The index is loaded by looking backwards at      * a TAR file. This method relies on an instance of {@link ReaderAtEnd}      * which is positioned at the end of the index in the TAR file.      *      * @param reader an instance of {@link ReaderAtEnd}.      * @return An instance of {@link Index}.      * @throws IOException           If an I/O error occurs while reading the      *                               index.      * @throws InvalidIndexException If a validation error occurs while checking      *                               the index.      */
specifier|public
name|Index
name|loadIndex
parameter_list|(
name|ReaderAtEnd
name|reader
parameter_list|)
throws|throws
name|IOException
throws|,
name|InvalidIndexException
block|{
switch|switch
condition|(
name|readMagic
argument_list|(
name|reader
argument_list|)
condition|)
block|{
case|case
name|IndexLoaderV1
operator|.
name|MAGIC
case|:
return|return
name|v1
operator|.
name|loadIndex
argument_list|(
name|reader
argument_list|)
return|;
case|case
name|IndexLoaderV2
operator|.
name|MAGIC
case|:
return|return
name|v2
operator|.
name|loadIndex
argument_list|(
name|reader
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|InvalidIndexException
argument_list|(
literal|"Unrecognized magic number"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

