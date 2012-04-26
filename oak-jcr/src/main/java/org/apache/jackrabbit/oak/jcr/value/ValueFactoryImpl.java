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
operator|.
name|value
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|namepath
operator|.
name|NameMapper
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
name|Paths
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
name|util
operator|.
name|Calendar
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link ValueFactory} interface based on the  * {@link CoreValueFactory} exposed by the  * {@link org.apache.jackrabbit.oak.api.ContentSession#getCoreValueFactory()}  * being aware of namespaces remapped on the editing session.  */
end_comment

begin_class
specifier|public
class|class
name|ValueFactoryImpl
implements|implements
name|ValueFactory
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
name|ValueFactoryImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|CoreValueFactory
name|factory
decl_stmt|;
specifier|private
specifier|final
name|NameMapper
name|nameMapper
decl_stmt|;
comment|/**      * Creates a new instance of {@code ValueFactory}.      *      * @param factory The core value factory.      * @param nameMapper The name mapping used for converting JCR names/paths to      * the internal representation.      */
specifier|public
name|ValueFactoryImpl
parameter_list|(
name|CoreValueFactory
name|factory
parameter_list|,
name|NameMapper
name|nameMapper
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|this
operator|.
name|nameMapper
operator|=
name|nameMapper
expr_stmt|;
block|}
specifier|public
name|CoreValueFactory
name|getCoreValueFactory
parameter_list|()
block|{
return|return
name|factory
return|;
block|}
specifier|public
name|Value
name|createValue
parameter_list|(
name|CoreValue
name|coreValue
parameter_list|)
block|{
return|return
operator|new
name|ValueImpl
argument_list|(
name|coreValue
argument_list|,
name|nameMapper
argument_list|)
return|;
block|}
specifier|public
name|CoreValue
name|getCoreValue
parameter_list|(
name|Value
name|jcrValue
parameter_list|)
block|{
name|ValueImpl
name|v
decl_stmt|;
if|if
condition|(
name|jcrValue
operator|instanceof
name|ValueImpl
condition|)
block|{
name|v
operator|=
operator|(
name|ValueImpl
operator|)
name|jcrValue
expr_stmt|;
block|}
else|else
block|{
comment|// TODO add proper implementation
try|try
block|{
switch|switch
condition|(
name|jcrValue
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|BINARY
case|:
name|v
operator|=
operator|(
name|ValueImpl
operator|)
name|createValue
argument_list|(
name|jcrValue
operator|.
name|getStream
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|v
operator|=
operator|(
name|ValueImpl
operator|)
name|createValue
argument_list|(
name|jcrValue
operator|.
name|getString
argument_list|()
argument_list|,
name|jcrValue
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not implemented yet..."
argument_list|)
throw|;
block|}
block|}
return|return
name|v
operator|.
name|unwrap
argument_list|()
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
name|CoreValue
name|cv
init|=
name|factory
operator|.
name|createValue
argument_list|(
name|value
argument_list|,
name|PropertyType
operator|.
name|STRING
argument_list|)
decl_stmt|;
return|return
operator|new
name|ValueImpl
argument_list|(
name|cv
argument_list|,
name|nameMapper
argument_list|)
return|;
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
name|CoreValue
name|cv
init|=
name|factory
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
operator|new
name|ValueImpl
argument_list|(
name|cv
argument_list|,
name|nameMapper
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
name|CoreValue
name|cv
init|=
name|factory
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
operator|new
name|ValueImpl
argument_list|(
name|cv
argument_list|,
name|nameMapper
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
name|CoreValue
name|cv
init|=
name|factory
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
operator|new
name|ValueImpl
argument_list|(
name|cv
argument_list|,
name|nameMapper
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
name|String
name|dateStr
init|=
name|ISO8601
operator|.
name|format
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|CoreValue
name|cv
init|=
name|factory
operator|.
name|createValue
argument_list|(
name|dateStr
argument_list|,
name|PropertyType
operator|.
name|DATE
argument_list|)
decl_stmt|;
return|return
operator|new
name|ValueImpl
argument_list|(
name|cv
argument_list|,
name|nameMapper
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
name|CoreValue
name|cv
init|=
name|factory
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
operator|new
name|ValueImpl
argument_list|(
name|cv
argument_list|,
name|nameMapper
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// JCR-2903
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
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
name|String
name|value
parameter_list|,
name|int
name|type
parameter_list|)
throws|throws
name|ValueFormatException
block|{
name|CoreValue
name|cv
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|NAME
condition|)
block|{
name|cv
operator|=
name|factory
operator|.
name|createValue
argument_list|(
name|nameMapper
operator|.
name|getOakName
argument_list|(
name|value
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
name|PropertyType
operator|.
name|PATH
condition|)
block|{
name|cv
operator|=
name|factory
operator|.
name|createValue
argument_list|(
name|Paths
operator|.
name|toOakPath
argument_list|(
name|value
argument_list|,
name|nameMapper
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|cv
operator|=
name|factory
operator|.
name|createValue
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ValueImpl
argument_list|(
name|cv
argument_list|,
name|nameMapper
argument_list|)
return|;
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
name|ValueImpl
name|value
init|=
operator|(
name|ValueImpl
operator|)
name|createValue
argument_list|(
name|stream
argument_list|)
decl_stmt|;
return|return
operator|new
name|BinaryImpl
argument_list|(
name|value
argument_list|)
return|;
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
return|return
name|createValue
argument_list|(
name|value
operator|.
name|getStream
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
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
name|CoreValue
name|cv
init|=
name|factory
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
operator|new
name|ValueImpl
argument_list|(
name|cv
argument_list|,
name|nameMapper
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
name|CoreValue
name|cv
init|=
name|factory
operator|.
name|createValue
argument_list|(
name|value
operator|.
name|getUUID
argument_list|()
argument_list|,
name|weak
condition|?
name|PropertyType
operator|.
name|WEAKREFERENCE
else|:
name|PropertyType
operator|.
name|REFERENCE
argument_list|)
decl_stmt|;
return|return
operator|new
name|ValueImpl
argument_list|(
name|cv
argument_list|,
name|nameMapper
argument_list|)
return|;
block|}
block|}
end_class

end_unit

