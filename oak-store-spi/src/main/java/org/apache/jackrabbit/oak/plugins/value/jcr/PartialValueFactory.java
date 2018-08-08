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
name|value
operator|.
name|jcr
operator|.
name|ValueImpl
operator|.
name|newValue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
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
name|Node
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
name|ValueFormatException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|nodetype
operator|.
name|NodeType
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
name|Lists
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
name|BlobDownloadOptions
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
name|UUIDUtils
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
name|JcrNameParser
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
name|JcrPathParser
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
name|BooleanPropertyState
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
name|DecimalPropertyState
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
name|DoublePropertyState
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
name|GenericPropertyState
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
name|LongPropertyState
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
name|PropertyStates
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
name|StringPropertyState
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
name|Conversions
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
name|util
operator|.
name|ISO8601
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

begin_comment
comment|/**  * A partial value factory implementation that only deals with in-memory values  * and can wrap a {@link Value} around a {@link PropertyState}.  */
end_comment

begin_class
specifier|public
class|class
name|PartialValueFactory
block|{
comment|/**      * This default blob access provider is a no-op implementation.      */
annotation|@
name|NotNull
specifier|public
specifier|static
specifier|final
name|BlobAccessProvider
name|DEFAULT_BLOB_ACCESS_PROVIDER
init|=
operator|new
name|DefaultBlobAccessProvider
argument_list|()
decl_stmt|;
annotation|@
name|NotNull
specifier|protected
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
annotation|@
name|NotNull
specifier|private
specifier|final
name|BlobAccessProvider
name|blobAccessProvider
decl_stmt|;
comment|/**      * Creates a new value factory stub using the given {@link NamePathMapper}.      * The factory instance created with this constructor does not have a      * {@link BlobAccessProvider} and any {@link Binary} retrieved from a      * {@link Value} returned by this factory instance will not provide a      * download URI.      *      * @param namePathMapper the name path mapper.      */
specifier|public
name|PartialValueFactory
parameter_list|(
annotation|@
name|NotNull
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
argument_list|(
name|namePathMapper
argument_list|,
name|DEFAULT_BLOB_ACCESS_PROVIDER
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new value factory stub using the given {@link NamePathMapper}      * and {@link BlobAccessProvider}.      *      * @param namePathMapper the name path mapper.      * @param blobAccessProvider the blob access provider.      */
specifier|public
name|PartialValueFactory
parameter_list|(
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
name|this
operator|.
name|namePathMapper
operator|=
name|checkNotNull
argument_list|(
name|namePathMapper
argument_list|)
expr_stmt|;
name|this
operator|.
name|blobAccessProvider
operator|=
name|checkNotNull
argument_list|(
name|blobAccessProvider
argument_list|)
expr_stmt|;
block|}
annotation|@
name|NotNull
specifier|public
name|BlobAccessProvider
name|getBlobAccessProvider
parameter_list|()
block|{
return|return
name|blobAccessProvider
return|;
block|}
comment|/**      * Utility method for creating a {@code Value} based on a      * {@code PropertyState}.      *      * @param property The property state      * @return New {@code Value} instance      * @throws IllegalArgumentException if {@code property.isArray()} is      *         {@code true}.      */
annotation|@
name|NotNull
specifier|public
name|Value
name|createValue
parameter_list|(
annotation|@
name|NotNull
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|newValue
argument_list|(
name|property
argument_list|,
name|namePathMapper
argument_list|,
name|blobAccessProvider
argument_list|)
return|;
block|}
comment|/**      * Utility method for creating {@code Value}s based on a      * {@code PropertyState}.      *      * @param property The property state      * @return A list of new {@code Value} instances      */
annotation|@
name|NotNull
specifier|public
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
parameter_list|)
block|{
name|List
argument_list|<
name|Value
argument_list|>
name|values
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
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
name|property
operator|.
name|count
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|values
operator|.
name|add
argument_list|(
name|newValue
argument_list|(
name|property
argument_list|,
name|i
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
comment|//-------------------------------------------------------< ValueFactory>---
annotation|@
name|NotNull
specifier|public
name|Value
name|createValue
parameter_list|(
annotation|@
name|NotNull
name|String
name|value
parameter_list|)
block|{
return|return
name|newValue
argument_list|(
name|StringPropertyState
operator|.
name|stringProperty
argument_list|(
literal|""
argument_list|,
name|value
argument_list|)
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|Value
name|createValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|newValue
argument_list|(
name|LongPropertyState
operator|.
name|createLongProperty
argument_list|(
literal|""
argument_list|,
name|value
argument_list|)
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|Value
name|createValue
parameter_list|(
name|double
name|value
parameter_list|)
block|{
return|return
name|newValue
argument_list|(
name|DoublePropertyState
operator|.
name|doubleProperty
argument_list|(
literal|""
argument_list|,
name|value
argument_list|)
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|Value
name|createValue
parameter_list|(
annotation|@
name|NotNull
name|Calendar
name|value
parameter_list|)
block|{
return|return
name|newValue
argument_list|(
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|""
argument_list|,
name|value
argument_list|)
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|Value
name|createValue
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
return|return
name|newValue
argument_list|(
name|BooleanPropertyState
operator|.
name|booleanProperty
argument_list|(
literal|""
argument_list|,
name|value
argument_list|)
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|Value
name|createValue
parameter_list|(
annotation|@
name|NotNull
name|Node
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|createValue
argument_list|(
name|value
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|Value
name|createValue
parameter_list|(
annotation|@
name|NotNull
name|Node
name|value
parameter_list|,
name|boolean
name|weak
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
operator|!
name|value
operator|.
name|isNodeType
argument_list|(
name|NodeType
operator|.
name|MIX_REFERENCEABLE
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Node is not referenceable: "
operator|+
name|value
operator|.
name|getPath
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|weak
condition|?
name|newValue
argument_list|(
name|GenericPropertyState
operator|.
name|weakreferenceProperty
argument_list|(
literal|""
argument_list|,
name|value
operator|.
name|getUUID
argument_list|()
argument_list|)
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
else|:
name|newValue
argument_list|(
name|GenericPropertyState
operator|.
name|referenceProperty
argument_list|(
literal|""
argument_list|,
name|value
operator|.
name|getUUID
argument_list|()
argument_list|)
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|Value
name|createValue
parameter_list|(
annotation|@
name|NotNull
name|BigDecimal
name|value
parameter_list|)
block|{
return|return
name|newValue
argument_list|(
name|DecimalPropertyState
operator|.
name|decimalProperty
argument_list|(
literal|""
argument_list|,
name|value
argument_list|)
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|public
name|Value
name|createValue
parameter_list|(
name|String
name|value
parameter_list|,
name|int
name|type
parameter_list|)
throws|throws
name|ValueFormatException
block|{
if|if
condition|(
name|value
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
try|try
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
return|return
name|createValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
return|return
name|newValue
argument_list|(
name|BinaryPropertyState
operator|.
name|binaryProperty
argument_list|(
literal|""
argument_list|,
name|value
argument_list|)
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
return|return
name|createValue
argument_list|(
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toLong
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
name|createValue
argument_list|(
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toDouble
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DATE
case|:
if|if
condition|(
name|ISO8601
operator|.
name|parse
argument_list|(
name|value
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Invalid date "
operator|+
name|value
argument_list|)
throw|;
block|}
return|return
name|newValue
argument_list|(
name|GenericPropertyState
operator|.
name|dateProperty
argument_list|(
literal|""
argument_list|,
name|value
argument_list|)
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
name|createValue
argument_list|(
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toBoolean
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|NAME
case|:
name|String
name|oakName
init|=
name|namePathMapper
operator|.
name|getOakNameOrNull
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|oakName
operator|==
literal|null
operator|||
operator|!
name|JcrNameParser
operator|.
name|validate
argument_list|(
name|oakName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Invalid name: "
operator|+
name|value
argument_list|)
throw|;
block|}
return|return
name|newValue
argument_list|(
name|GenericPropertyState
operator|.
name|nameProperty
argument_list|(
literal|""
argument_list|,
name|oakName
argument_list|)
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|PATH
case|:
name|String
name|oakValue
init|=
name|value
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"["
argument_list|)
operator|&&
name|value
operator|.
name|endsWith
argument_list|(
literal|"]"
argument_list|)
condition|)
block|{
comment|// identifier path; do no change
block|}
else|else
block|{
name|oakValue
operator|=
name|namePathMapper
operator|.
name|getOakPath
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|oakValue
operator|==
literal|null
operator|||
operator|!
name|JcrPathParser
operator|.
name|validate
argument_list|(
name|oakValue
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Invalid path: "
operator|+
name|value
argument_list|)
throw|;
block|}
block|}
return|return
name|newValue
argument_list|(
name|GenericPropertyState
operator|.
name|pathProperty
argument_list|(
literal|""
argument_list|,
name|oakValue
argument_list|)
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|REFERENCE
case|:
if|if
condition|(
operator|!
name|UUIDUtils
operator|.
name|isValidUUID
argument_list|(
name|value
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Invalid reference value "
operator|+
name|value
argument_list|)
throw|;
block|}
return|return
name|newValue
argument_list|(
name|GenericPropertyState
operator|.
name|referenceProperty
argument_list|(
literal|""
argument_list|,
name|value
argument_list|)
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|WEAKREFERENCE
case|:
if|if
condition|(
operator|!
name|UUIDUtils
operator|.
name|isValidUUID
argument_list|(
name|value
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Invalid weak reference value "
operator|+
name|value
argument_list|)
throw|;
block|}
return|return
name|newValue
argument_list|(
name|GenericPropertyState
operator|.
name|weakreferenceProperty
argument_list|(
literal|""
argument_list|,
name|value
argument_list|)
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|URI
case|:
operator|new
name|URI
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|newValue
argument_list|(
name|GenericPropertyState
operator|.
name|uriProperty
argument_list|(
literal|""
argument_list|,
name|value
argument_list|)
argument_list|,
name|namePathMapper
argument_list|,
name|getBlobAccessProvider
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
name|createValue
argument_list|(
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toDecimal
argument_list|()
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Invalid type: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|NumberFormatException
decl||
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Invalid value "
operator|+
name|value
operator|+
literal|" for type "
operator|+
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|type
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * A {@link BlobAccessProvider} implementation that does not support direct      * binary up- or download.      */
specifier|private
specifier|static
class|class
name|DefaultBlobAccessProvider
implements|implements
name|BlobAccessProvider
block|{
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|BlobUpload
name|initiateBlobUpload
parameter_list|(
name|long
name|maxUploadSizeInBytes
parameter_list|,
name|int
name|maxNumberOfURIs
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|Blob
name|completeBlobUpload
parameter_list|(
annotation|@
name|NotNull
name|String
name|uploadToken
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|URI
name|getDownloadURI
parameter_list|(
annotation|@
name|NotNull
name|Blob
name|blob
parameter_list|,
annotation|@
name|NotNull
name|BlobDownloadOptions
name|downloadOptions
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

