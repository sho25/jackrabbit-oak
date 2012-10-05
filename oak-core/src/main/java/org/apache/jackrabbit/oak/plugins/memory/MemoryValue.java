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
name|Arrays
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
name|PropertyType
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

begin_class
specifier|public
specifier|abstract
class|class
name|MemoryValue
implements|implements
name|CoreValue
block|{
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
name|MemoryValue
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|long
name|getLong
parameter_list|()
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|getString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getDouble
parameter_list|()
block|{
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|getString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getBoolean
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Unsupported conversion."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|BigDecimal
name|getDecimal
parameter_list|()
block|{
return|return
operator|new
name|BigDecimal
argument_list|(
name|getString
argument_list|()
argument_list|)
return|;
block|}
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
name|IllegalStateException
argument_list|(
literal|"UTF-8 is not supported"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|getString
argument_list|()
operator|.
name|length
argument_list|()
return|;
block|}
comment|//----------------------------------------------------------< Comparable>
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|CoreValue
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|0
return|;
block|}
name|int
name|type
init|=
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|!=
name|o
operator|.
name|getType
argument_list|()
condition|)
block|{
return|return
name|o
operator|.
name|getType
argument_list|()
operator|-
name|type
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|LONG
condition|)
block|{
return|return
name|Long
operator|.
name|signum
argument_list|(
name|getLong
argument_list|()
operator|-
name|o
operator|.
name|getLong
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|DOUBLE
condition|)
block|{
return|return
name|Double
operator|.
name|compare
argument_list|(
name|getDouble
argument_list|()
argument_list|,
name|o
operator|.
name|getDouble
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|DECIMAL
condition|)
block|{
return|return
name|getDecimal
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|getDecimal
argument_list|()
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|BOOLEAN
condition|)
block|{
return|return
operator|(
name|getBoolean
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
operator|-
operator|(
name|o
operator|.
name|getBoolean
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
return|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|DATE
condition|)
block|{
name|Calendar
name|a
init|=
name|ISO8601
operator|.
name|parse
argument_list|(
name|getString
argument_list|()
argument_list|)
decl_stmt|;
name|Calendar
name|b
init|=
name|ISO8601
operator|.
name|parse
argument_list|(
name|o
operator|.
name|getString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
literal|null
operator|&&
name|b
operator|!=
literal|null
condition|)
block|{
return|return
name|a
operator|.
name|compareTo
argument_list|(
name|b
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|getString
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|getString
argument_list|()
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
return|return
name|compare
argument_list|(
name|getNewStream
argument_list|()
argument_list|,
name|o
operator|.
name|getNewStream
argument_list|()
argument_list|)
condition|?
literal|0
else|:
literal|1
return|;
block|}
else|else
block|{
return|return
name|getString
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|getString
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|compare
parameter_list|(
name|InputStream
name|in2
parameter_list|,
name|InputStream
name|in1
parameter_list|)
block|{
try|try
block|{
try|try
block|{
name|byte
index|[]
name|buf1
init|=
operator|new
name|byte
index|[
literal|0x1000
index|]
decl_stmt|;
name|byte
index|[]
name|buf2
init|=
operator|new
name|byte
index|[
literal|0x1000
index|]
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|int
name|read1
init|=
name|ByteStreams
operator|.
name|read
argument_list|(
name|in1
argument_list|,
name|buf1
argument_list|,
literal|0
argument_list|,
literal|0x1000
argument_list|)
decl_stmt|;
name|int
name|read2
init|=
name|ByteStreams
operator|.
name|read
argument_list|(
name|in2
argument_list|,
name|buf2
argument_list|,
literal|0
argument_list|,
literal|0x1000
argument_list|)
decl_stmt|;
if|if
condition|(
name|read1
operator|!=
name|read2
operator|||
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|buf1
argument_list|,
name|buf2
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|read1
operator|!=
literal|0x1000
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
finally|finally
block|{
name|in1
operator|.
name|close
argument_list|()
expr_stmt|;
name|in2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error comparing binary values"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|//--------------------------------------------------------------< Object>
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getType
argument_list|()
operator|^
name|getString
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|o
operator|instanceof
name|CoreValue
condition|)
block|{
return|return
name|compareTo
argument_list|(
operator|(
name|CoreValue
operator|)
name|o
argument_list|)
operator|==
literal|0
return|;
block|}
else|else
block|{
return|return
literal|false
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
name|getString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

