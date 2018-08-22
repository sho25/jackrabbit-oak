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
name|value
operator|.
name|jcr
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
name|checkNotNull
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
name|net
operator|.
name|URI
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
name|PropertyType
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
name|javax
operator|.
name|jcr
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ValueFormatException
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
name|api
operator|.
name|JackrabbitValueFactory
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
name|api
operator|.
name|ReferenceBinary
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
name|api
operator|.
name|binary
operator|.
name|BinaryUpload
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
name|api
operator|.
name|PropertyValue
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
name|Root
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
name|blob
operator|.
name|BlobAccessProvider
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
name|blob
operator|.
name|BlobUpload
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
name|commons
operator|.
name|PerfLogger
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
name|namepath
operator|.
name|NamePathMapper
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
name|memory
operator|.
name|BinaryPropertyState
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
name|memory
operator|.
name|PropertyValues
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
name|value
operator|.
name|ErrorValue
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
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
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
comment|/**  * Implementation of {@link ValueFactory} interface.  */
end_comment

begin_class
specifier|public
class|class
name|ValueFactoryImpl
extends|extends
name|PartialValueFactory
implements|implements
name|JackrabbitValueFactory
block|{
specifier|private
specifier|static
specifier|final
name|PerfLogger
name|binOpsLogger
init|=
operator|new
name|PerfLogger
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
literal|"org.apache.jackrabbit.oak.jcr.operations.binary.perf"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|Root
name|root
decl_stmt|;
comment|/**      * Creates a new instance of {@code ValueFactory}.      *      * @param root the root instance for creating binary values      * @param namePathMapper The name/path mapping used for converting JCR names/paths to      * @param blobAccessProvider The blob access provider      * the internal representation.      */
specifier|public
name|ValueFactoryImpl
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|,
annotation|@
name|NotNull
name|BlobAccessProvider
name|blobAccessProvider
parameter_list|)
block|{
name|super
argument_list|(
name|namePathMapper
argument_list|,
name|blobAccessProvider
argument_list|)
expr_stmt|;
name|this
operator|.
name|root
operator|=
name|checkNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new instance of {@code ValueFactory}. The {@link Value}s      * created by this value factory instance will not be backed by a blob      * access provider and never return a download URI for a binary value.      *      * @param root the root instance for creating binary values      * @param namePathMapper The name/path mapping used for converting JCR names/paths to      * the internal representation.      */
specifier|public
name|ValueFactoryImpl
parameter_list|(
annotation|@
name|NotNull
name|Root
name|root
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
argument_list|(
name|root
argument_list|,
name|namePathMapper
argument_list|,
name|DEFAULT_BLOB_ACCESS_PROVIDER
argument_list|)
expr_stmt|;
block|}
comment|/**      * Utility method for creating a {@code Value} based on a      * {@code PropertyState}. The {@link Value} instance created by this factory      * method will not be backed with a {@link BlobAccessProvider} and the      * {@link Binary} retrieved from the {@link Value} does not provide a      * download URI, even if the underlying blob store supports it.      *      * @param property  The property state      * @param namePathMapper The name/path mapping used for converting JCR      *          names/paths to the internal representation.      * @return  New {@code Value} instance      * @throws IllegalArgumentException if {@code property.isArray()} is {@code true}.      * @deprecated use {@link PartialValueFactory#createValue(PropertyState)} instead.      */
annotation|@
name|Deprecated
annotation|@
name|NotNull
specifier|public
specifier|static
name|Value
name|createValue
parameter_list|(
annotation|@
name|NotNull
name|PropertyState
name|property
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|PartialValueFactory
argument_list|(
name|namePathMapper
argument_list|)
operator|.
name|createValue
argument_list|(
name|property
argument_list|)
return|;
block|}
comment|/**      * Utility method for creating a {@code Value} based on a      * {@code PropertyValue}. The {@link Value} instance created by this factory      * method will not be backed with a {@link BlobAccessProvider} and the      * {@link Binary} retrieved from the {@link Value} does not provide a      * download URI, even if the underlying blob store supports it.      *      * Utility method for creating a {@code Value} based on a {@code PropertyValue}.      * @param property  The property value      * @param namePathMapper The name/path mapping used for converting JCR names/paths to      * the internal representation.      * @return  New {@code Value} instance      * @throws IllegalArgumentException if {@code property.isArray()} is {@code true}.      * @deprecated use {@link PartialValueFactory#createValue(PropertyState)} instead.      */
annotation|@
name|Deprecated
annotation|@
name|NotNull
specifier|public
specifier|static
name|Value
name|createValue
parameter_list|(
annotation|@
name|NotNull
name|PropertyValue
name|property
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|PropertyValues
operator|.
name|create
argument_list|(
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|ps
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Failed to convert the specified property value to a property state."
argument_list|)
throw|;
block|}
return|return
operator|new
name|PartialValueFactory
argument_list|(
name|namePathMapper
argument_list|)
operator|.
name|createValue
argument_list|(
name|ps
argument_list|)
return|;
block|}
comment|/**      * Utility method for creating {@code Value}s based on a      * {@code PropertyState}. The {@link Value} instances created by this factory      * method will not be backed with a {@link BlobAccessProvider} and the      * {@link Binary} retrieved from the {@link Value} does not provide a      * download URI, even if the underlying blob store supports it.      *      * @param property  The property state      * @param namePathMapper The name/path mapping used for converting JCR names/paths to      * the internal representation.      * @return  A list of new {@code Value} instances      * @deprecated use {@link PartialValueFactory#createValues(PropertyState)} instead.      */
annotation|@
name|Deprecated
annotation|@
name|NotNull
specifier|public
specifier|static
name|List
argument_list|<
name|Value
argument_list|>
name|createValues
parameter_list|(
annotation|@
name|NotNull
name|PropertyState
name|property
parameter_list|,
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|PartialValueFactory
argument_list|(
name|namePathMapper
argument_list|)
operator|.
name|createValues
argument_list|(
name|property
argument_list|)
return|;
block|}
comment|//-------------------------------------------------------< ValueFactory>---
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|Value
name|createValue
parameter_list|(
annotation|@
name|NotNull
name|InputStream
name|value
parameter_list|)
block|{
try|try
block|{
return|return
name|createBinaryValue
argument_list|(
name|value
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
operator|new
name|ErrorValue
argument_list|(
name|e
argument_list|,
name|PropertyType
operator|.
name|BINARY
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
return|return
operator|new
name|ErrorValue
argument_list|(
name|e
argument_list|,
name|PropertyType
operator|.
name|BINARY
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|Value
name|createValue
parameter_list|(
annotation|@
name|NotNull
name|Binary
name|value
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|value
operator|instanceof
name|BinaryImpl
condition|)
block|{
comment|// No need to create the value again if we have it already underlying the binary
return|return
operator|(
operator|(
name|BinaryImpl
operator|)
name|value
operator|)
operator|.
name|getBinaryValue
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|instanceof
name|ReferenceBinary
condition|)
block|{
name|String
name|reference
init|=
operator|(
operator|(
name|ReferenceBinary
operator|)
name|value
operator|)
operator|.
name|getReference
argument_list|()
decl_stmt|;
name|Blob
name|blob
init|=
name|root
operator|.
name|getBlob
argument_list|(
name|reference
argument_list|)
decl_stmt|;
if|if
condition|(
name|blob
operator|!=
literal|null
condition|)
block|{
return|return
name|createBinaryValue
argument_list|(
name|blob
argument_list|)
return|;
block|}
block|}
name|InputStream
name|stream
init|=
name|value
operator|.
name|getStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|stream
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"null"
argument_list|)
throw|;
block|}
return|return
name|createBinaryValue
argument_list|(
name|stream
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
return|return
operator|new
name|ErrorValue
argument_list|(
name|e
argument_list|,
name|PropertyType
operator|.
name|BINARY
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
operator|new
name|ErrorValue
argument_list|(
name|e
argument_list|,
name|PropertyType
operator|.
name|BINARY
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|Binary
name|createBinary
parameter_list|(
annotation|@
name|NotNull
name|InputStream
name|stream
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
return|return
operator|new
name|BinaryImpl
argument_list|(
name|createBinaryValue
argument_list|(
name|stream
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
name|RepositoryException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|BinaryUpload
name|initiateBinaryUpload
parameter_list|(
name|long
name|maxSize
parameter_list|,
name|int
name|maxParts
parameter_list|)
block|{
name|BlobUpload
name|upload
init|=
name|getBlobAccessProvider
argument_list|()
operator|.
name|initiateBlobUpload
argument_list|(
name|maxSize
argument_list|,
name|maxParts
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|upload
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|BinaryUpload
argument_list|()
block|{
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|Iterable
argument_list|<
name|URI
argument_list|>
name|getUploadURIs
parameter_list|()
block|{
return|return
name|upload
operator|.
name|getUploadURIs
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMinPartSize
parameter_list|()
block|{
return|return
name|upload
operator|.
name|getMinPartSize
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getMaxPartSize
parameter_list|()
block|{
return|return
name|upload
operator|.
name|getMaxPartSize
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|NotNull
specifier|public
name|String
name|getUploadToken
parameter_list|()
block|{
return|return
name|upload
operator|.
name|getUploadToken
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Binary
name|completeBinaryUpload
parameter_list|(
annotation|@
name|NotNull
name|String
name|uploadToken
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|createBinary
argument_list|(
name|getBlobAccessProvider
argument_list|()
operator|.
name|completeBlobUpload
argument_list|(
name|uploadToken
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|ValueImpl
name|createBinaryValue
parameter_list|(
annotation|@
name|NotNull
name|InputStream
name|value
parameter_list|)
throws|throws
name|IOException
throws|,
name|RepositoryException
block|{
name|long
name|start
init|=
name|binOpsLogger
operator|.
name|start
argument_list|()
decl_stmt|;
name|Blob
name|blob
init|=
name|root
operator|.
name|createBlob
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|binOpsLogger
operator|.
name|end
argument_list|(
name|start
argument_list|,
operator|-
literal|1
argument_list|,
literal|"Created binary property of size [{}]"
argument_list|,
name|blob
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|createBinaryValue
argument_list|(
name|blob
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|private
name|ValueImpl
name|createBinaryValue
parameter_list|(
annotation|@
name|NotNull
name|Blob
name|blob
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|ValueImpl
argument_list|(
name|BinaryPropertyState
operator|.
name|binaryProperty
argument_list|(
literal|""
argument_list|,
name|blob
argument_list|)
argument_list|,
name|getNamePathMapper
argument_list|()
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Nullable
specifier|public
name|Binary
name|createBinary
parameter_list|(
name|Blob
name|blob
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
literal|null
operator|!=
name|blob
condition|?
name|createBinaryValue
argument_list|(
name|blob
argument_list|)
operator|.
name|getBinary
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Nullable
specifier|public
name|Blob
name|getBlob
parameter_list|(
name|Binary
name|binary
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|binary
operator|instanceof
name|BinaryImpl
condition|)
block|{
return|return
operator|(
operator|(
name|BinaryImpl
operator|)
name|binary
operator|)
operator|.
name|getBinaryValue
argument_list|()
operator|.
name|getBlob
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

