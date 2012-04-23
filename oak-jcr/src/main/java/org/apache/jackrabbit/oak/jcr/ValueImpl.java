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
name|jcr
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
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
name|util
operator|.
name|ISO8601
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
name|io
operator|.
name|UnsupportedEncodingException
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
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_comment
comment|/**  * ValueImpl...  */
end_comment

begin_class
specifier|public
class|class
name|ValueImpl
implements|implements
name|Value
block|{
comment|/**      * logger instance      */
specifier|private
specifier|static
specifier|final
name|Logger
name|log
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
name|CoreValue
name|value
decl_stmt|;
comment|// TODO need utility to convert the internal NAME/PATH format to JCR format
specifier|private
specifier|final
name|ValueFactoryImpl
operator|.
name|DummyNamePathResolver
name|resolver
decl_stmt|;
comment|/**      * Constructs a<code>ValueImpl</code> object representing an SPI      *<codeQValue</code>.      *      * @param value the value object this<code>ValueImpl</code> should represent      * @param resolver      */
specifier|public
name|ValueImpl
parameter_list|(
name|CoreValue
name|value
parameter_list|,
name|ValueFactoryImpl
operator|.
name|DummyNamePathResolver
name|resolver
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|resolver
operator|=
name|resolver
expr_stmt|;
block|}
comment|//--------------------------------------------------------------< Value>---
comment|/**      * @see javax.jcr.Value#getType()      */
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|value
operator|.
name|getType
argument_list|()
return|;
block|}
comment|/**      * @see javax.jcr.Value#getBoolean()      */
specifier|public
name|boolean
name|getBoolean
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|getType
argument_list|()
operator|==
name|PropertyType
operator|.
name|STRING
operator|||
name|getType
argument_list|()
operator|==
name|PropertyType
operator|.
name|BINARY
operator|||
name|getType
argument_list|()
operator|==
name|PropertyType
operator|.
name|BOOLEAN
condition|)
block|{
return|return
name|value
operator|.
name|getBoolean
argument_list|()
return|;
block|}
else|else
block|{
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
specifier|public
name|Calendar
name|getDate
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Calendar
name|cal
decl_stmt|;
switch|switch
condition|(
name|getType
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
case|case
name|PropertyType
operator|.
name|LONG
case|:
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
name|cal
operator|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT+00:00"
argument_list|)
argument_list|)
expr_stmt|;
name|cal
operator|.
name|setTimeInMillis
argument_list|(
name|getLong
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|cal
operator|=
name|ISO8601
operator|.
name|parse
argument_list|(
name|getString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|cal
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Not a date string: "
operator|+
name|getString
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|cal
return|;
block|}
comment|/**      * @see javax.jcr.Value#getDecimal()      */
specifier|public
name|BigDecimal
name|getDecimal
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
return|return
name|value
operator|.
name|getDecimal
argument_list|()
return|;
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
comment|/**      * @see javax.jcr.Value#getDouble()      */
specifier|public
name|double
name|getDouble
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
return|return
name|value
operator|.
name|getDouble
argument_list|()
return|;
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
comment|/**      * @see javax.jcr.Value#getLong()      */
specifier|public
name|long
name|getLong
parameter_list|()
throws|throws
name|RepositoryException
block|{
try|try
block|{
return|return
name|value
operator|.
name|getLong
argument_list|()
return|;
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
comment|/**      * @see javax.jcr.Value#getString()      */
specifier|public
name|String
name|getString
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
name|NAME
case|:
return|return
name|resolver
operator|.
name|getJCRName
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|PATH
case|:
return|return
name|resolver
operator|.
name|getJCRPath
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
name|InputStream
name|stream
init|=
name|getStream
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|IOUtils
operator|.
name|toString
argument_list|(
name|stream
argument_list|,
literal|"UTF-8"
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
literal|"conversion from stream to string failed"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
default|default:
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**      * @see javax.jcr.Value#getStream()      */
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|IllegalStateException
throws|,
name|RepositoryException
block|{
name|InputStream
name|stream
decl_stmt|;
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
case|case
name|PropertyType
operator|.
name|PATH
case|:
try|try
block|{
name|stream
operator|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|getString
argument_list|()
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
literal|"UTF-8 is not supported"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
break|break;
default|default:
name|stream
operator|=
name|value
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
argument_list|()
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
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|ValueImpl
condition|)
block|{
return|return
name|value
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|ValueImpl
operator|)
name|obj
operator|)
operator|.
name|value
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
return|return
name|value
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|//------------------------------------------------------------< Binary>----
specifier|private
class|class
name|BinaryImpl
implements|implements
name|Binary
block|{
annotation|@
name|Override
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|RepositoryException
block|{
switch|switch
condition|(
name|value
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|NAME
case|:
case|case
name|PropertyType
operator|.
name|PATH
case|:
comment|// need to respect namespace remapping
try|try
block|{
specifier|final
name|String
name|strValue
init|=
name|getString
argument_list|()
decl_stmt|;
return|return
operator|new
name|ByteArrayInputStream
argument_list|(
name|strValue
operator|.
name|getBytes
argument_list|(
literal|"utf-8"
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RepositoryException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
default|default:
return|return
name|value
operator|.
name|getNewStream
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|long
name|position
parameter_list|)
throws|throws
name|IOException
throws|,
name|RepositoryException
block|{
comment|// TODO
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"implementation missing"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|()
throws|throws
name|RepositoryException
block|{
switch|switch
condition|(
name|value
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|NAME
case|:
case|case
name|PropertyType
operator|.
name|PATH
case|:
comment|// need to respect namespace remapping
return|return
name|getString
argument_list|()
operator|.
name|length
argument_list|()
return|;
default|default:
return|return
name|value
operator|.
name|length
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|dispose
parameter_list|()
block|{
comment|// nothing to do
block|}
block|}
block|}
end_class

end_unit

