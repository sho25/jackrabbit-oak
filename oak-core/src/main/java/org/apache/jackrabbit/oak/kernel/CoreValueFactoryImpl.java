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
name|kernel
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|oak
operator|.
name|api
operator|.
name|CoreValueFactory
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
name|PropertyType
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

begin_comment
comment|/**  * CoreValueFactoryImpl... TODO javadoc  */
end_comment

begin_class
class|class
name|CoreValueFactoryImpl
implements|implements
name|CoreValueFactory
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
name|CoreValueFactoryImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|MicroKernel
name|mk
decl_stmt|;
comment|// TODO: currently public for query tests -> see todo there...
specifier|public
name|CoreValueFactoryImpl
parameter_list|(
name|MicroKernel
name|mk
parameter_list|)
block|{
name|this
operator|.
name|mk
operator|=
name|mk
expr_stmt|;
block|}
comment|//-----------------------------------------< org.apache.jackrabbit.oak.api.CoreValueFactory>---
annotation|@
name|Override
specifier|public
name|CoreValue
name|createValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|CoreValueImpl
argument_list|(
name|value
argument_list|,
name|PropertyType
operator|.
name|STRING
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CoreValue
name|createValue
parameter_list|(
name|double
name|value
parameter_list|)
block|{
return|return
operator|new
name|CoreValueImpl
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CoreValue
name|createValue
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
operator|new
name|CoreValueImpl
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CoreValue
name|createValue
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
return|return
operator|new
name|CoreValueImpl
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CoreValue
name|createValue
parameter_list|(
name|BigDecimal
name|value
parameter_list|)
block|{
return|return
operator|new
name|CoreValueImpl
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CoreValue
name|createValue
parameter_list|(
name|InputStream
name|value
parameter_list|)
block|{
name|String
name|binaryID
init|=
name|mk
operator|.
name|write
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
operator|new
name|CoreValueImpl
argument_list|(
operator|new
name|BinaryValue
argument_list|(
name|binaryID
argument_list|,
name|mk
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CoreValue
name|createValue
parameter_list|(
name|String
name|value
parameter_list|,
name|int
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
name|BinaryValue
name|bv
init|=
operator|new
name|BinaryValue
argument_list|(
name|value
argument_list|,
name|mk
argument_list|)
decl_stmt|;
return|return
operator|new
name|CoreValueImpl
argument_list|(
name|bv
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|CoreValueImpl
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

