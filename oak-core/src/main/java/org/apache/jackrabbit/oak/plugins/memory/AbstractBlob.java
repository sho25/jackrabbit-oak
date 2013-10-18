begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|memory
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
name|io
operator|.
name|InputStream
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
name|hash
operator|.
name|HashCode
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
name|hash
operator|.
name|Hashing
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
name|io
operator|.
name|ByteStreams
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
name|io
operator|.
name|InputSupplier
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
name|Blob
import|;
end_import

begin_comment
comment|/**  * Abstract base class for {@link Blob} implementations.  * This base class provides default implementations for  * {@code hashCode} and {@code equals}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractBlob
implements|implements
name|Blob
block|{
specifier|private
specifier|static
name|InputSupplier
argument_list|<
name|InputStream
argument_list|>
name|supplier
parameter_list|(
specifier|final
name|Blob
name|blob
parameter_list|)
block|{
return|return
operator|new
name|InputSupplier
argument_list|<
name|InputStream
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InputStream
name|getInput
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|blob
operator|.
name|getNewStream
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
name|boolean
name|equal
parameter_list|(
name|Blob
name|a
parameter_list|,
name|Blob
name|b
parameter_list|)
block|{
try|try
block|{
return|return
name|ByteStreams
operator|.
name|equal
argument_list|(
name|supplier
argument_list|(
name|a
argument_list|)
argument_list|,
name|supplier
argument_list|(
name|b
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Blob equality check failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|HashCode
name|calculateSha256
parameter_list|(
specifier|final
name|Blob
name|blob
parameter_list|)
block|{
name|AbstractBlob
name|ab
decl_stmt|;
if|if
condition|(
name|blob
operator|instanceof
name|AbstractBlob
condition|)
block|{
name|ab
operator|=
operator|(
operator|(
name|AbstractBlob
operator|)
name|blob
operator|)
expr_stmt|;
block|}
else|else
block|{
name|ab
operator|=
operator|new
name|AbstractBlob
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|blob
operator|.
name|length
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getNewStream
parameter_list|()
block|{
return|return
name|blob
operator|.
name|getNewStream
argument_list|()
return|;
block|}
block|}
expr_stmt|;
block|}
return|return
name|ab
operator|.
name|getSha256
argument_list|()
return|;
block|}
specifier|private
name|HashCode
name|hashCode
decl_stmt|;
comment|// synchronized access
specifier|protected
name|AbstractBlob
parameter_list|(
name|HashCode
name|hashCode
parameter_list|)
block|{
name|this
operator|.
name|hashCode
operator|=
name|hashCode
expr_stmt|;
block|}
specifier|protected
name|AbstractBlob
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|HashCode
name|getSha256
parameter_list|()
block|{
comment|// Blobs are immutable so we can safely cache the hash
if|if
condition|(
name|hashCode
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|hashCode
operator|=
name|ByteStreams
operator|.
name|hash
argument_list|(
name|supplier
argument_list|(
name|this
argument_list|)
argument_list|,
name|Hashing
operator|.
name|sha256
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Hash calculation failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|hashCode
return|;
block|}
comment|/**      * This hash code implementation returns the hash code of the underlying stream      * @return      */
specifier|protected
name|byte
index|[]
name|sha256
parameter_list|()
block|{
return|return
name|getSha256
argument_list|()
operator|.
name|asBytes
argument_list|()
return|;
block|}
comment|/**      * To {@code Blob} instances are considered equal iff they have the      * same SHA-256 hash code  are equal.      * @param other      * @return      */
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|instanceof
name|AbstractBlob
condition|)
block|{
name|AbstractBlob
name|that
init|=
operator|(
name|AbstractBlob
operator|)
name|other
decl_stmt|;
comment|// optimize the comparison if both this and the other blob
comment|// already have pre-computed SHA-256 hash codes
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|hashCode
operator|!=
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|that
init|)
block|{
if|if
condition|(
name|that
operator|.
name|hashCode
operator|!=
literal|null
condition|)
block|{
return|return
name|hashCode
operator|.
name|equals
argument_list|(
name|that
operator|.
name|hashCode
argument_list|)
return|;
block|}
block|}
block|}
block|}
block|}
return|return
name|other
operator|instanceof
name|Blob
operator|&&
name|equal
argument_list|(
name|this
argument_list|,
operator|(
name|Blob
operator|)
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
literal|0
return|;
comment|// see Blob javadoc
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getSha256
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

