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
name|BlobFactory
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
name|identifier
operator|.
name|IdentifierManager
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
name|spi
operator|.
name|query
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
name|util
operator|.
name|ISO8601
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link ValueFactory} interface.  */
end_comment

begin_class
specifier|public
class|class
name|ValueFactoryImpl
implements|implements
name|ValueFactory
block|{
specifier|private
specifier|final
name|BlobFactory
name|blobFactory
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
comment|/**      * Creates a new instance of {@code ValueFactory}.      *      * @param blobFactory The factory for creation of binary values      * @param namePathMapper The name/path mapping used for converting JCR names/paths to      * the internal representation.      */
specifier|public
name|ValueFactoryImpl
parameter_list|(
name|BlobFactory
name|blobFactory
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
name|this
operator|.
name|blobFactory
operator|=
name|blobFactory
expr_stmt|;
name|this
operator|.
name|namePathMapper
operator|=
name|namePathMapper
expr_stmt|;
block|}
comment|/**      * Utility method for creating a {@code Value} based on a {@code PropertyState}.      * @param property  The property state      * @param namePathMapper The name/path mapping used for converting JCR names/paths to      * the internal representation.      * @return  New {@code Value} instance      * @throws IllegalArgumentException if {@code property.isArray()} is {@code true}.      */
specifier|public
specifier|static
name|Value
name|createValue
parameter_list|(
name|PropertyState
name|property
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|ValueImpl
argument_list|(
name|property
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
comment|/**      * Utility method for creating a {@code Value} based on a {@code PropertyValue}.      * @param property  The property value      * @param namePathMapper The name/path mapping used for converting JCR names/paths to      * the internal representation.      * @return  New {@code Value} instance      * @throws IllegalArgumentException if {@code property.isArray()} is {@code true}.      */
specifier|public
specifier|static
name|Value
name|createValue
parameter_list|(
name|PropertyValue
name|property
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
block|{
return|return
operator|new
name|ValueImpl
argument_list|(
name|PropertyValues
operator|.
name|create
argument_list|(
name|property
argument_list|)
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
comment|/**      * Utility method for creating {@code Value}s based on a {@code PropertyState}.      * @param property  The property state      * @param namePathMapper The name/path mapping used for converting JCR names/paths to      * the internal representation.      * @return  A list of new {@code Value} instances      */
specifier|public
specifier|static
name|List
argument_list|<
name|Value
argument_list|>
name|createValues
parameter_list|(
name|PropertyState
name|property
parameter_list|,
name|NamePathMapper
name|namePathMapper
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
operator|new
name|ValueImpl
argument_list|(
name|property
argument_list|,
name|i
argument_list|,
name|namePathMapper
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
name|Override
specifier|public
name|Value
name|createValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|ValueImpl
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
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Value
name|createValue
parameter_list|(
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
block|}
annotation|@
name|Override
specifier|public
name|Value
name|createValue
parameter_list|(
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
else|else
block|{
return|return
name|createBinaryValue
argument_list|(
name|value
operator|.
name|getStream
argument_list|()
argument_list|)
return|;
block|}
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
specifier|public
name|Value
name|createValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
operator|new
name|ValueImpl
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
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Value
name|createValue
parameter_list|(
name|double
name|value
parameter_list|)
block|{
return|return
operator|new
name|ValueImpl
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
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Value
name|createValue
parameter_list|(
name|Calendar
name|value
parameter_list|)
block|{
return|return
operator|new
name|ValueImpl
argument_list|(
name|LongPropertyState
operator|.
name|createDateProperty
argument_list|(
literal|""
argument_list|,
name|value
argument_list|)
argument_list|,
name|namePathMapper
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Value
name|createValue
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
return|return
operator|new
name|ValueImpl
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
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Value
name|createValue
parameter_list|(
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
name|Override
specifier|public
name|Value
name|createValue
parameter_list|(
name|Node
name|value
parameter_list|,
name|boolean
name|weak
parameter_list|)
throws|throws
name|RepositoryException
block|{
return|return
name|weak
condition|?
operator|new
name|ValueImpl
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
argument_list|)
else|:
operator|new
name|ValueImpl
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
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Value
name|createValue
parameter_list|(
name|BigDecimal
name|value
parameter_list|)
block|{
return|return
operator|new
name|ValueImpl
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
argument_list|)
return|;
block|}
annotation|@
name|Override
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
operator|new
name|ValueImpl
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
operator|new
name|ValueImpl
argument_list|(
name|LongPropertyState
operator|.
name|createDateProperty
argument_list|(
literal|""
argument_list|,
name|value
argument_list|)
argument_list|,
name|namePathMapper
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
operator|new
name|ValueImpl
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
block|}
if|if
condition|(
name|oakValue
operator|==
literal|null
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
return|return
operator|new
name|ValueImpl
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
name|IdentifierManager
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
operator|new
name|ValueImpl
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
name|IdentifierManager
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
operator|new
name|ValueImpl
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
operator|new
name|ValueImpl
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
catch|catch
parameter_list|(
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
annotation|@
name|Override
specifier|public
name|Binary
name|createBinary
parameter_list|(
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
specifier|private
name|ValueImpl
name|createBinaryValue
parameter_list|(
name|InputStream
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|Blob
name|blob
init|=
name|blobFactory
operator|.
name|createBlob
argument_list|(
name|value
argument_list|)
decl_stmt|;
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
name|namePathMapper
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< ErrorValue>---
comment|/**      * Instances of this class represent a {@code Value} which couldn't be retrieved.      * All its accessors throw a {@code RepositoryException}.      */
specifier|private
specifier|static
class|class
name|ErrorValue
implements|implements
name|Value
block|{
specifier|private
specifier|final
name|Exception
name|exception
decl_stmt|;
specifier|private
specifier|final
name|int
name|type
decl_stmt|;
specifier|private
name|ErrorValue
parameter_list|(
name|Exception
name|exception
parameter_list|,
name|int
name|type
parameter_list|)
block|{
name|this
operator|.
name|exception
operator|=
name|exception
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
throws|throws
name|RepositoryException
block|{
throw|throw
name|createException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|RepositoryException
block|{
throw|throw
name|createException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Binary
name|getBinary
parameter_list|()
throws|throws
name|RepositoryException
block|{
throw|throw
name|createException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getLong
parameter_list|()
throws|throws
name|RepositoryException
block|{
throw|throw
name|createException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getDouble
parameter_list|()
throws|throws
name|RepositoryException
block|{
throw|throw
name|createException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|BigDecimal
name|getDecimal
parameter_list|()
throws|throws
name|RepositoryException
block|{
throw|throw
name|createException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Calendar
name|getDate
parameter_list|()
throws|throws
name|RepositoryException
block|{
throw|throw
name|createException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getBoolean
parameter_list|()
throws|throws
name|RepositoryException
block|{
throw|throw
name|createException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|private
name|RepositoryException
name|createException
parameter_list|()
block|{
return|return
operator|new
name|RepositoryException
argument_list|(
literal|"Inaccessible value"
argument_list|,
name|exception
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Inaccessible value: "
operator|+
name|exception
operator|.
name|getMessage
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

