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
name|checkArgument
import|;
end_import

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
name|checkState
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
name|util
operator|.
name|Calendar
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
name|base
operator|.
name|Objects
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
name|JackrabbitValue
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
name|BinaryDownloadOptions
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
name|IllegalRepositoryStateException
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
name|Type
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
name|OakValue
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
comment|/**  * Implementation of {@link Value} based on {@code PropertyState}.  */
end_comment

begin_class
class|class
name|ValueImpl
implements|implements
name|JackrabbitValue
implements|,
name|OakValue
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
name|ValueImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|PropertyState
name|propertyState
decl_stmt|;
specifier|private
specifier|final
name|Type
argument_list|<
name|?
argument_list|>
name|type
decl_stmt|;
specifier|private
specifier|final
name|int
name|index
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|namePathMapper
decl_stmt|;
specifier|private
specifier|final
name|BlobAccessProvider
name|blobAccessProvider
decl_stmt|;
specifier|private
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
comment|/**      * Create a new {@code Value} instance      * @param property  The property state this instance is based on      * @param index  The index      * @param namePathMapper The name/path mapping used for converting JCR names/paths to      * the internal representation.      * @param blobAccessProvider The blob access provider      * @throws IllegalArgumentException if {@code index< propertyState.count()}      * @throws RepositoryException if the underlying node state cannot be accessed      */
specifier|private
name|ValueImpl
parameter_list|(
annotation|@
name|NotNull
name|PropertyState
name|property
parameter_list|,
name|int
name|index
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
throws|throws
name|RepositoryException
block|{
name|checkArgument
argument_list|(
name|index
operator|<
name|property
operator|.
name|count
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|propertyState
operator|=
name|checkNotNull
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|getType
argument_list|(
name|property
argument_list|)
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
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
comment|/**      * Create a new {@code Value} instance      * @param property  The property state this instance is based on      * @param namePathMapper The name/path mapping used for converting JCR names/paths to      * the internal representation.      * @param blobAccessProvider The blob access provider      * @throws IllegalArgumentException if {@code property.isArray()} is {@code true}.      * @throws RepositoryException if the underlying node state cannot be accessed      */
name|ValueImpl
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
parameter_list|,
annotation|@
name|NotNull
name|BlobAccessProvider
name|blobAccessProvider
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|this
argument_list|(
name|checkSingleValued
argument_list|(
name|property
argument_list|)
argument_list|,
literal|0
argument_list|,
name|namePathMapper
argument_list|,
name|checkNotNull
argument_list|(
name|blobAccessProvider
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|PropertyState
name|checkSingleValued
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
name|checkArgument
argument_list|(
operator|!
name|property
operator|.
name|isArray
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|property
return|;
block|}
comment|/**      * Create a new {@code Value} instance      * @param property  The property state this instance is based on      * @param index  The index      * @param namePathMapper The name/path mapping used for converting JCR names/paths to      * the internal representation.      * @param blobAccessProvider The blob access provider      * @throws IllegalArgumentException if {@code index< propertyState.count()}      */
annotation|@
name|NotNull
specifier|static
name|Value
name|newValue
parameter_list|(
annotation|@
name|NotNull
name|PropertyState
name|property
parameter_list|,
name|int
name|index
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
try|try
block|{
return|return
operator|new
name|ValueImpl
argument_list|(
name|property
argument_list|,
name|index
argument_list|,
name|namePathMapper
argument_list|,
name|blobAccessProvider
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
argument_list|)
return|;
block|}
block|}
comment|/**      * Create a new {@code Value} instance      * @param property  The property state this instance is based on      * @param namePathMapper The name/path mapping used for converting JCR names/paths to      * the internal representation.      * @param blobAccessProvider The blob access provider      * @throws IllegalArgumentException if {@code property.isArray()} is {@code true}.      */
annotation|@
name|NotNull
specifier|static
name|Value
name|newValue
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
parameter_list|,
annotation|@
name|NotNull
name|BlobAccessProvider
name|blobAccessProvider
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|ValueImpl
argument_list|(
name|property
argument_list|,
literal|0
argument_list|,
name|namePathMapper
argument_list|,
name|blobAccessProvider
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
argument_list|)
return|;
block|}
block|}
comment|//-----------------------------------------------------------< OakValue>---
specifier|public
name|Blob
name|getBlob
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|,
name|index
argument_list|)
return|;
block|}
comment|/**      * Same as {@link #getString()} unless that names and paths are returned in their      * Oak representation instead of being mapped to their JCR representation.      * @return  A String representation of the value of this property.      */
specifier|public
name|String
name|getOakString
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|index
argument_list|)
return|;
block|}
comment|//--------------------------------------------------------------< Value>---
comment|/**      * @see javax.jcr.Value#getType()      */
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
operator|.
name|tag
argument_list|()
return|;
block|}
comment|/**      * @see javax.jcr.Value#getBoolean()      */
annotation|@
name|Override
specifier|public
name|boolean
name|getBoolean
parameter_list|()
throws|throws
name|RepositoryException
block|{
switch|switch
condition|(
name|getType
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
case|case
name|PropertyType
operator|.
name|BINARY
case|:
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|index
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Incompatible type "
operator|+
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see javax.jcr.Value#getDate()      */
annotation|@
name|Override
specifier|public
name|Calendar
name|getDate
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
switch|switch
condition|(
name|getType
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
case|case
name|PropertyType
operator|.
name|BINARY
case|:
case|case
name|PropertyType
operator|.
name|DATE
case|:
name|String
name|value
init|=
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|,
name|index
argument_list|)
decl_stmt|;
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|value
argument_list|)
operator|.
name|toCalendar
argument_list|()
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
name|Conversions
operator|.
name|convert
argument_list|(
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
name|index
argument_list|)
argument_list|)
operator|.
name|toCalendar
argument_list|()
return|;
default|default:
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Incompatible type "
operator|+
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Error converting value to date"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see javax.jcr.Value#getDecimal()      */
annotation|@
name|Override
specifier|public
name|BigDecimal
name|getDecimal
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
switch|switch
condition|(
name|getType
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
case|case
name|PropertyType
operator|.
name|BINARY
case|:
case|case
name|PropertyType
operator|.
name|LONG
case|:
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
case|case
name|PropertyType
operator|.
name|DATE
case|:
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
name|getValue
argument_list|(
name|Type
operator|.
name|DECIMAL
argument_list|,
name|index
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Incompatible type "
operator|+
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Error converting value to decimal"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see javax.jcr.Value#getDouble()      */
annotation|@
name|Override
specifier|public
name|double
name|getDouble
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
switch|switch
condition|(
name|getType
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
case|case
name|PropertyType
operator|.
name|BINARY
case|:
case|case
name|PropertyType
operator|.
name|LONG
case|:
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
case|case
name|PropertyType
operator|.
name|DATE
case|:
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
name|getValue
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|index
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Incompatible type "
operator|+
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Error converting value to double"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see javax.jcr.Value#getLong()      */
annotation|@
name|Override
specifier|public
name|long
name|getLong
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
switch|switch
condition|(
name|getType
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
case|case
name|PropertyType
operator|.
name|BINARY
case|:
case|case
name|PropertyType
operator|.
name|LONG
case|:
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
case|case
name|PropertyType
operator|.
name|DATE
case|:
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
name|index
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Incompatible type "
operator|+
name|PropertyType
operator|.
name|nameFromValue
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Error converting value to long"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see javax.jcr.Value#getString()      */
annotation|@
name|Override
specifier|public
name|String
name|getString
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|checkState
argument_list|(
name|getType
argument_list|()
operator|!=
name|PropertyType
operator|.
name|BINARY
operator|||
name|stream
operator|==
literal|null
argument_list|,
literal|"getStream has previously been called on this Value instance. "
operator|+
literal|"In this case a new Value instance must be acquired in order to successfully call this method."
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|getType
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|NAME
case|:
return|return
name|namePathMapper
operator|.
name|getJcrName
argument_list|(
name|getOakString
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|PATH
case|:
name|String
name|s
init|=
name|getOakString
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|startsWith
argument_list|(
literal|"["
argument_list|)
operator|&&
name|s
operator|.
name|endsWith
argument_list|(
literal|"]"
argument_list|)
condition|)
block|{
comment|// identifier paths are returned as-is (JCR 2.0, 3.4.3.1)
return|return
name|s
return|;
block|}
else|else
block|{
return|return
name|namePathMapper
operator|.
name|getJcrPath
argument_list|(
name|s
argument_list|)
return|;
block|}
default|default:
return|return
name|getOakString
argument_list|()
return|;
block|}
block|}
comment|/**      * @see javax.jcr.Value#getStream()      */
annotation|@
name|Override
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|IllegalStateException
throws|,
name|RepositoryException
block|{
if|if
condition|(
name|stream
operator|==
literal|null
condition|)
block|{
name|stream
operator|=
name|getBlob
argument_list|()
operator|.
name|getNewStream
argument_list|()
expr_stmt|;
block|}
return|return
name|stream
return|;
block|}
comment|/**      * @see javax.jcr.Value#getBinary()      */
annotation|@
name|Override
specifier|public
name|Binary
name|getBinary
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|BinaryImpl
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContentIdentity
parameter_list|()
block|{
try|try
block|{
return|return
name|getBlob
argument_list|()
operator|.
name|getContentIdentity
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
literal|"Error getting content identity"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
comment|//-------------------------------------------------------------< Object>---
comment|/**      * @see Object#equals(Object)      */
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
operator|instanceof
name|ValueImpl
condition|)
block|{
name|ValueImpl
name|that
init|=
operator|(
name|ValueImpl
operator|)
name|other
decl_stmt|;
name|Type
argument_list|<
name|?
argument_list|>
name|thisType
init|=
name|this
operator|.
name|type
decl_stmt|;
if|if
condition|(
name|thisType
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|thisType
operator|=
name|thisType
operator|.
name|getBaseType
argument_list|()
expr_stmt|;
block|}
name|Type
argument_list|<
name|?
argument_list|>
name|thatType
init|=
name|that
operator|.
name|type
decl_stmt|;
if|if
condition|(
name|thatType
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|thatType
operator|=
name|thatType
operator|.
name|getBaseType
argument_list|()
expr_stmt|;
block|}
try|try
block|{
return|return
name|thisType
operator|==
name|thatType
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|getValue
argument_list|(
name|thatType
argument_list|,
name|index
argument_list|)
argument_list|,
name|that
operator|.
name|getValue
argument_list|(
name|thatType
argument_list|,
name|that
operator|.
name|index
argument_list|)
argument_list|)
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
literal|"Error while comparing values"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|/**      * @see Object#hashCode()      */
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|getType
argument_list|()
operator|==
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
return|return
name|getValue
argument_list|(
name|Type
operator|.
name|BINARY
argument_list|,
name|index
argument_list|)
operator|.
name|hashCode
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|index
argument_list|)
operator|.
name|hashCode
argument_list|()
return|;
block|}
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
literal|"Error while calculating hash code"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
return|return
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|index
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
name|e
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
annotation|@
name|Nullable
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
name|BinaryDownloadOptions
name|downloadOptions
parameter_list|)
block|{
if|if
condition|(
name|blobAccessProvider
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|blobAccessProvider
operator|.
name|getDownloadURI
argument_list|(
name|blob
argument_list|,
operator|new
name|BlobDownloadOptions
argument_list|(
name|downloadOptions
operator|.
name|getMediaType
argument_list|()
argument_list|,
name|downloadOptions
operator|.
name|getCharacterEncoding
argument_list|()
argument_list|,
name|downloadOptions
operator|.
name|getFileName
argument_list|()
argument_list|,
name|downloadOptions
operator|.
name|getDispositionType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
parameter_list|<
name|T
parameter_list|>
name|T
name|getValue
parameter_list|(
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|int
name|index
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
return|return
name|propertyState
operator|.
name|getValue
argument_list|(
name|type
argument_list|,
name|index
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalRepositoryStateException
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
specifier|static
name|Type
argument_list|<
name|?
argument_list|>
name|getType
parameter_list|(
name|PropertyState
name|property
parameter_list|)
throws|throws
name|RepositoryException
block|{
try|try
block|{
return|return
name|property
operator|.
name|getType
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IllegalRepositoryStateException
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
block|}
end_class

end_unit

