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
name|http
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|math
operator|.
name|BigDecimal
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
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonGenerator
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
name|Tree
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|mime
operator|.
name|MediaType
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
name|api
operator|.
name|Type
operator|.
name|BINARIES
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
name|api
operator|.
name|Type
operator|.
name|BOOLEANS
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
name|api
operator|.
name|Type
operator|.
name|DECIMALS
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
name|api
operator|.
name|Type
operator|.
name|DOUBLES
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
name|api
operator|.
name|Type
operator|.
name|LONGS
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
name|api
operator|.
name|Type
operator|.
name|STRINGS
import|;
end_import

begin_class
class|class
name|JsonRepresentation
implements|implements
name|Representation
block|{
specifier|private
specifier|final
name|MediaType
name|type
decl_stmt|;
specifier|private
specifier|final
name|JsonFactory
name|factory
decl_stmt|;
specifier|public
name|JsonRepresentation
parameter_list|(
name|MediaType
name|type
parameter_list|,
name|JsonFactory
name|factory
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MediaType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|render
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|JsonGenerator
name|generator
init|=
name|startResponse
argument_list|(
name|response
argument_list|)
decl_stmt|;
name|render
argument_list|(
name|tree
argument_list|,
name|generator
argument_list|)
expr_stmt|;
name|generator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|render
parameter_list|(
name|PropertyState
name|property
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|JsonGenerator
name|generator
init|=
name|startResponse
argument_list|(
name|response
argument_list|)
decl_stmt|;
name|render
argument_list|(
name|property
argument_list|,
name|generator
argument_list|)
expr_stmt|;
name|generator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|JsonGenerator
name|startResponse
parameter_list|(
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|response
operator|.
name|setContentType
argument_list|(
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|factory
operator|.
name|createGenerator
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|render
parameter_list|(
name|Tree
name|tree
parameter_list|,
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
block|{
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|tree
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|property
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|render
argument_list|(
name|property
argument_list|,
name|generator
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Tree
name|child
range|:
name|tree
operator|.
name|getChildren
argument_list|()
control|)
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|child
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|render
parameter_list|(
name|PropertyState
name|property
parameter_list|,
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|generator
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
name|renderValue
argument_list|(
name|property
argument_list|,
name|generator
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|renderValue
argument_list|(
name|property
argument_list|,
name|generator
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|renderValue
parameter_list|(
name|PropertyState
name|property
parameter_list|,
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: Type info?
name|int
name|type
init|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|BOOLEAN
condition|)
block|{
for|for
control|(
name|boolean
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|BOOLEANS
argument_list|)
control|)
block|{
name|generator
operator|.
name|writeBoolean
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|BigDecimal
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|DECIMALS
argument_list|)
control|)
block|{
name|generator
operator|.
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|double
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|DOUBLES
argument_list|)
control|)
block|{
name|generator
operator|.
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
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
for|for
control|(
name|long
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|LONGS
argument_list|)
control|)
block|{
name|generator
operator|.
name|writeNumber
argument_list|(
name|value
argument_list|)
expr_stmt|;
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
name|ByteArrayOutputStream
name|buffer
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
for|for
control|(
name|Blob
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|BINARIES
argument_list|)
control|)
block|{
name|InputStream
name|stream
init|=
name|value
operator|.
name|getNewStream
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|n
init|=
name|stream
operator|.
name|read
argument_list|(
name|b
argument_list|)
decl_stmt|;
while|while
condition|(
name|n
operator|!=
operator|-
literal|1
condition|)
block|{
name|buffer
operator|.
name|write
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
name|n
operator|=
name|stream
operator|.
name|read
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|generator
operator|.
name|writeBinary
argument_list|(
name|buffer
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|String
name|value
range|:
name|property
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
control|)
block|{
name|generator
operator|.
name|writeString
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

