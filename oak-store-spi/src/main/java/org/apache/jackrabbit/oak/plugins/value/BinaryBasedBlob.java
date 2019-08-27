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
name|value
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
name|javax
operator|.
name|jcr
operator|.
name|Binary
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

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * This Blob implementation is based on an underlying {@link javax.jcr.Binary}.  *<p>  * Any error accessing the underlying binary in {@link #getNewStream()} will be  * deferred to the returned input stream.  */
end_comment

begin_class
specifier|public
class|class
name|BinaryBasedBlob
implements|implements
name|Blob
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BinaryBasedBlob
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Binary
name|binary
decl_stmt|;
specifier|public
name|BinaryBasedBlob
parameter_list|(
name|Binary
name|binary
parameter_list|)
block|{
name|this
operator|.
name|binary
operator|=
name|binary
expr_stmt|;
block|}
comment|/**      * Delegates to {@link Binary#getStream()} and returns an input stream the always      * throws an {@code IOException} if the underlying binary failed to produce one.      */
annotation|@
name|NotNull
annotation|@
name|Override
specifier|public
name|InputStream
name|getNewStream
parameter_list|()
block|{
try|try
block|{
return|return
name|binary
operator|.
name|getStream
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|RepositoryException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error retrieving stream from binary"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
operator|new
name|InputStream
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|;
block|}
block|}
comment|/**      * Delegates to {@link Binary#getSize()} and returns -1 if that fails.      */
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
try|try
block|{
return|return
name|binary
operator|.
name|getSize
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error determining length of binary"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|/**      * @return  {@code null}      */
annotation|@
name|Override
specifier|public
name|String
name|getReference
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @return  {@code null}      */
annotation|@
name|Override
specifier|public
name|String
name|getContentIdentity
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

