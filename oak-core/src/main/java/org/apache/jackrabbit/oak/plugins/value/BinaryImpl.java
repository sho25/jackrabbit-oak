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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Objects
operator|.
name|toStringHelper
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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|ReferenceBinary
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
comment|/**  * TODO document  */
end_comment

begin_class
class|class
name|BinaryImpl
implements|implements
name|ReferenceBinary
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
name|BinaryImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ValueImpl
name|value
decl_stmt|;
name|BinaryImpl
parameter_list|(
name|ValueImpl
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
name|ValueImpl
name|getBinaryValue
parameter_list|()
block|{
return|return
name|value
operator|.
name|getType
argument_list|()
operator|==
name|PropertyType
operator|.
name|BINARY
condition|?
name|value
else|:
literal|null
return|;
block|}
comment|//-------------------------------------------------------------< Binary>---
annotation|@
name|Override
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|value
operator|.
name|getBlob
argument_list|()
operator|.
name|getNewStream
argument_list|()
return|;
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
name|InputStream
name|stream
init|=
name|getStream
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|position
operator|!=
name|stream
operator|.
name|skip
argument_list|(
name|position
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't skip to position "
operator|+
name|position
argument_list|)
throw|;
block|}
return|return
name|stream
operator|.
name|read
argument_list|(
name|b
argument_list|)
return|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|value
operator|.
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
name|getBlob
argument_list|()
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
comment|//---------------------------------------------------< ReferenceBinary>--
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|String
name|getReference
parameter_list|()
block|{
try|try
block|{
return|return
name|value
operator|.
name|getBlob
argument_list|()
operator|.
name|getReference
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
literal|"Error getting binary reference"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
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
name|ReferenceBinary
condition|)
block|{
return|return
name|Objects
operator|.
name|equal
argument_list|(
name|getReference
argument_list|()
argument_list|,
operator|(
operator|(
name|ReferenceBinary
operator|)
name|other
operator|)
operator|.
name|getReference
argument_list|()
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
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hashCode
argument_list|(
name|getReference
argument_list|()
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
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|addValue
argument_list|(
name|value
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

