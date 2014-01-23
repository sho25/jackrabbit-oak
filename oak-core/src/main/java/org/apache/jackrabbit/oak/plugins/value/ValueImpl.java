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
name|namepath
operator|.
name|NamePathMapper
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
name|checkState
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link Value} based on {@code PropertyState}.  */
end_comment

begin_class
specifier|public
class|class
name|ValueImpl
implements|implements
name|Value
block|{
specifier|public
specifier|static
name|Blob
name|getBlob
parameter_list|(
name|Value
name|value
parameter_list|)
block|{
name|checkState
argument_list|(
name|value
operator|instanceof
name|ValueImpl
argument_list|)
expr_stmt|;
return|return
operator|(
operator|(
name|ValueImpl
operator|)
name|value
operator|)
operator|.
name|getBlob
argument_list|()
return|;
block|}
specifier|private
specifier|final
name|PropertyState
name|propertyState
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
name|InputStream
name|stream
init|=
literal|null
decl_stmt|;
comment|/**      * Create a new {@code Value} instance      * @param property  The property state this instance is based on      * @param index  The index      * @param namePathMapper The name/path mapping used for converting JCR names/paths to      * the internal representation.      * @throws IllegalArgumentException if {@code index< propertyState.count()}      */
name|ValueImpl
parameter_list|(
name|PropertyState
name|property
parameter_list|,
name|int
name|index
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
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
name|property
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
name|namePathMapper
expr_stmt|;
block|}
comment|/**      * Create a new {@code Value} instance      * @param property  The property state this instance is based on      * @param namePathMapper The name/path mapping used for converting JCR names/paths to      * the internal representation.      * @throws IllegalArgumentException if {@code property.isArray()} is {@code true}.      */
name|ValueImpl
parameter_list|(
name|PropertyState
name|property
parameter_list|,
name|NamePathMapper
name|namePathMapper
parameter_list|)
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
argument_list|)
expr_stmt|;
block|}
name|Blob
name|getBlob
parameter_list|()
block|{
return|return
name|propertyState
operator|.
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
block|{
return|return
name|propertyState
operator|.
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
name|propertyState
operator|.
name|getType
argument_list|()
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
name|propertyState
operator|.
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
name|propertyState
operator|.
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
name|propertyState
operator|.
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
name|propertyState
operator|.
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
name|propertyState
operator|.
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
name|propertyState
operator|.
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
literal|"Error converting value to double"
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
name|type
init|=
name|propertyState
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|type
operator|=
name|type
operator|.
name|getBaseType
argument_list|()
expr_stmt|;
block|}
return|return
name|type
operator|.
name|tag
argument_list|()
operator|==
name|that
operator|.
name|propertyState
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|propertyState
operator|.
name|getValue
argument_list|(
name|type
argument_list|,
name|index
argument_list|)
argument_list|,
name|that
operator|.
name|propertyState
operator|.
name|getValue
argument_list|(
name|type
argument_list|,
name|that
operator|.
name|index
argument_list|)
argument_list|)
return|;
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
name|propertyState
operator|.
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
name|getOakString
argument_list|()
operator|.
name|hashCode
argument_list|()
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
return|return
name|getOakString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

