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
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Tree
operator|.
name|Status
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
name|jcr
operator|.
name|value
operator|.
name|ValueConverter
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
name|value
operator|.
name|ValueHelper
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
name|annotation
operator|.
name|Nonnull
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
name|ItemNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|ItemVisitor
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
name|PathNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Property
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
name|PropertyDefinition
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
name|ArrayList
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

begin_comment
comment|/**  * {@code PropertyImpl}...  */
end_comment

begin_class
specifier|public
class|class
name|PropertyImpl
extends|extends
name|ItemImpl
implements|implements
name|Property
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
name|PropertyImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|PropertyDelegate
name|dlg
decl_stmt|;
name|PropertyImpl
parameter_list|(
name|PropertyDelegate
name|dlg
parameter_list|)
block|{
name|super
argument_list|(
name|dlg
operator|.
name|getSessionDelegate
argument_list|()
argument_list|,
name|dlg
argument_list|)
expr_stmt|;
name|this
operator|.
name|dlg
operator|=
name|dlg
expr_stmt|;
block|}
comment|//---------------------------------------------------------------< Item>---
comment|/**      * @see javax.jcr.Item#isNode()      */
annotation|@
name|Override
specifier|public
name|boolean
name|isNode
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * @see javax.jcr.Item#getParent()      */
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Node
name|getParent
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
operator|new
name|NodeImpl
argument_list|(
name|dlg
operator|.
name|getParent
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @see javax.jcr.Item#isNew()      */
annotation|@
name|Override
specifier|public
name|boolean
name|isNew
parameter_list|()
block|{
return|return
name|dlg
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|NEW
return|;
block|}
comment|/**      * @see javax.jcr.Item#isModified() ()      */
annotation|@
name|Override
specifier|public
name|boolean
name|isModified
parameter_list|()
block|{
return|return
name|dlg
operator|.
name|getStatus
argument_list|()
operator|==
name|Status
operator|.
name|MODIFIED
return|;
block|}
comment|/**      * @see javax.jcr.Item#isNew()      */
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
name|dlg
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see javax.jcr.Item#accept(javax.jcr.ItemVisitor)      */
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|ItemVisitor
name|visitor
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|//-----------------------------------------------------------< Property>---
comment|/**      * @see Property#setValue(Value)      */
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
name|int
name|valueType
init|=
operator|(
name|value
operator|!=
literal|null
operator|)
condition|?
name|value
operator|.
name|getType
argument_list|()
else|:
name|PropertyType
operator|.
name|UNDEFINED
decl_stmt|;
name|int
name|reqType
init|=
name|getRequiredType
argument_list|(
name|valueType
argument_list|)
decl_stmt|;
name|setValue
argument_list|(
name|value
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see Property#setValue(Value[])      */
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|Value
index|[]
name|values
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
comment|// assert equal types for all values entries
name|int
name|valueType
init|=
name|PropertyType
operator|.
name|UNDEFINED
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Value
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
comment|// skip null values as those will be purged later
continue|continue;
block|}
if|if
condition|(
name|valueType
operator|==
name|PropertyType
operator|.
name|UNDEFINED
condition|)
block|{
name|valueType
operator|=
name|value
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|valueType
operator|!=
name|value
operator|.
name|getType
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
literal|"Inhomogeneous type of values ("
operator|+
name|this
operator|+
literal|')'
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ValueFormatException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
block|}
name|int
name|reqType
init|=
name|getRequiredType
argument_list|(
name|valueType
argument_list|)
decl_stmt|;
name|setValues
argument_list|(
name|values
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see Property#setValue(String)      */
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
name|int
name|reqType
init|=
name|getRequiredType
argument_list|(
name|PropertyType
operator|.
name|STRING
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|setValue
argument_list|(
literal|null
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see Property#setValue(String[])      */
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|String
index|[]
name|values
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
name|int
name|reqType
init|=
name|getRequiredType
argument_list|(
name|PropertyType
operator|.
name|STRING
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|setValues
argument_list|(
literal|null
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
argument_list|<
name|Value
argument_list|>
name|vs
init|=
operator|new
name|ArrayList
argument_list|<
name|Value
argument_list|>
argument_list|(
name|values
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|vs
operator|.
name|add
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|setValues
argument_list|(
name|vs
operator|.
name|toArray
argument_list|(
operator|new
name|Value
index|[
name|vs
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see Property#setValue(InputStream)      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|InputStream
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
name|int
name|reqType
init|=
name|getRequiredType
argument_list|(
name|PropertyType
operator|.
name|BINARY
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|setValue
argument_list|(
literal|null
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see Property#setValue(Binary)      */
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|Binary
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
name|int
name|reqType
init|=
name|getRequiredType
argument_list|(
name|PropertyType
operator|.
name|BINARY
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|setValue
argument_list|(
literal|null
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see Property#setValue(long)      */
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|long
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
name|int
name|reqType
init|=
name|getRequiredType
argument_list|(
name|PropertyType
operator|.
name|LONG
argument_list|)
decl_stmt|;
name|setValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see Property#setValue(double)      */
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|double
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
name|int
name|reqType
init|=
name|getRequiredType
argument_list|(
name|PropertyType
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|setValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see Property#setValue(BigDecimal)      */
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|BigDecimal
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
name|int
name|reqType
init|=
name|getRequiredType
argument_list|(
name|PropertyType
operator|.
name|DECIMAL
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|setValue
argument_list|(
literal|null
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see Property#setValue(Calendar)      */
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|Calendar
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
name|int
name|reqType
init|=
name|getRequiredType
argument_list|(
name|PropertyType
operator|.
name|DATE
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|setValue
argument_list|(
literal|null
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see Property#setValue(boolean)      */
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|boolean
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
name|int
name|reqType
init|=
name|getRequiredType
argument_list|(
name|PropertyType
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
name|setValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see Property#setValue(javax.jcr.Node)      */
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
name|Node
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
name|int
name|reqType
init|=
name|getRequiredType
argument_list|(
name|PropertyType
operator|.
name|REFERENCE
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|setValue
argument_list|(
literal|null
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setValue
argument_list|(
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|value
argument_list|)
argument_list|,
name|reqType
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Value
name|getValue
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
if|if
condition|(
name|isMultiple
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
name|this
operator|+
literal|" is multi-valued."
argument_list|)
throw|;
block|}
return|return
name|ValueConverter
operator|.
name|toValue
argument_list|(
name|dlg
operator|.
name|getValue
argument_list|()
argument_list|,
name|sessionDelegate
argument_list|)
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Value
index|[]
name|getValues
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isMultiple
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
name|this
operator|+
literal|" is not multi-valued."
argument_list|)
throw|;
block|}
return|return
name|ValueConverter
operator|.
name|toValues
argument_list|(
name|dlg
operator|.
name|getValues
argument_list|()
argument_list|,
name|sessionDelegate
argument_list|)
return|;
block|}
comment|/**      * @see Property#getString()      */
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|String
name|getString
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getString
argument_list|()
return|;
block|}
comment|/**      * @see Property#getStream()      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|InputStream
name|getStream
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getStream
argument_list|()
return|;
block|}
comment|/**      * @see javax.jcr.Property#getBinary()      */
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Binary
name|getBinary
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getBinary
argument_list|()
return|;
block|}
comment|/**      * @see Property#getLong()      */
annotation|@
name|Override
specifier|public
name|long
name|getLong
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getLong
argument_list|()
return|;
block|}
comment|/**      * @see Property#getDouble()      */
annotation|@
name|Override
specifier|public
name|double
name|getDouble
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getDouble
argument_list|()
return|;
block|}
comment|/**      * @see Property#getDecimal()      */
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|BigDecimal
name|getDecimal
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getDecimal
argument_list|()
return|;
block|}
comment|/**      * @see Property#getDate()      */
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Calendar
name|getDate
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getDate
argument_list|()
return|;
block|}
comment|/**      * @see Property#getBoolean()      */
annotation|@
name|Override
specifier|public
name|boolean
name|getBoolean
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getBoolean
argument_list|()
return|;
block|}
comment|/**      * @see javax.jcr.Property#getNode()      */
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Node
name|getNode
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Value
name|value
init|=
name|getValue
argument_list|()
decl_stmt|;
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
name|REFERENCE
case|:
case|case
name|PropertyType
operator|.
name|WEAKREFERENCE
case|:
return|return
name|getSession
argument_list|()
operator|.
name|getNodeByIdentifier
argument_list|(
name|value
operator|.
name|getString
argument_list|()
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|PATH
case|:
case|case
name|PropertyType
operator|.
name|NAME
case|:
name|String
name|path
init|=
name|value
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
literal|"["
argument_list|)
operator|&&
name|path
operator|.
name|endsWith
argument_list|(
literal|"]"
argument_list|)
condition|)
block|{
comment|// TODO OAK-23
comment|// TODO correct for NAME?
return|return
name|getSession
argument_list|()
operator|.
name|getNodeByIdentifier
argument_list|(
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|path
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
try|try
block|{
return|return
operator|(
name|path
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
operator|)
condition|?
name|getSession
argument_list|()
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
else|:
name|getParent
argument_list|()
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ItemNotFoundException
argument_list|(
name|path
argument_list|)
throw|;
block|}
block|}
case|case
name|PropertyType
operator|.
name|STRING
case|:
try|try
block|{
name|Value
name|refValue
init|=
name|ValueHelper
operator|.
name|convert
argument_list|(
name|value
argument_list|,
name|PropertyType
operator|.
name|REFERENCE
argument_list|,
name|getValueFactory
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|getSession
argument_list|()
operator|.
name|getNodeByIdentifier
argument_list|(
name|refValue
operator|.
name|getString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ItemNotFoundException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|RepositoryException
name|e
parameter_list|)
block|{
comment|// try if STRING value can be interpreted as PATH value
name|Value
name|pathValue
init|=
name|ValueHelper
operator|.
name|convert
argument_list|(
name|value
argument_list|,
name|PropertyType
operator|.
name|PATH
argument_list|,
name|getValueFactory
argument_list|()
argument_list|)
decl_stmt|;
name|path
operator|=
name|pathValue
operator|.
name|getString
argument_list|()
expr_stmt|;
try|try
block|{
return|return
operator|(
name|path
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
operator|)
condition|?
name|getSession
argument_list|()
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
else|:
name|getParent
argument_list|()
operator|.
name|getNode
argument_list|(
name|path
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|ItemNotFoundException
argument_list|(
name|pathValue
operator|.
name|getString
argument_list|()
argument_list|)
throw|;
block|}
block|}
default|default:
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Property value cannot be converted to a PATH, REFERENCE or WEAKREFERENCE"
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see javax.jcr.Property#getProperty()      */
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|Property
name|getProperty
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Value
name|value
init|=
name|getValue
argument_list|()
decl_stmt|;
name|Value
name|pathValue
init|=
name|ValueHelper
operator|.
name|convert
argument_list|(
name|value
argument_list|,
name|PropertyType
operator|.
name|PATH
argument_list|,
name|getValueFactory
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|path
init|=
name|pathValue
operator|.
name|getString
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|(
name|path
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
operator|)
condition|?
name|getSession
argument_list|()
operator|.
name|getProperty
argument_list|(
name|path
argument_list|)
else|:
name|getParent
argument_list|()
operator|.
name|getProperty
argument_list|(
name|path
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PathNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ItemNotFoundException
argument_list|(
name|path
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see javax.jcr.Property#getLength()      */
annotation|@
name|Override
specifier|public
name|long
name|getLength
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|getLength
argument_list|(
name|getValue
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * @see javax.jcr.Property#getLengths()      */
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|long
index|[]
name|getLengths
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Value
index|[]
name|values
init|=
name|getValues
argument_list|()
decl_stmt|;
name|long
index|[]
name|lengths
init|=
operator|new
name|long
index|[
name|values
operator|.
name|length
index|]
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
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|lengths
index|[
name|i
index|]
operator|=
name|getLength
argument_list|(
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|lengths
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nonnull
specifier|public
name|PropertyDefinition
name|getDefinition
parameter_list|()
throws|throws
name|RepositoryException
block|{
return|return
name|dlg
operator|.
name|getDefinition
argument_list|()
return|;
block|}
comment|/**      * @see javax.jcr.Property#getType()      */
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|isMultiple
argument_list|()
condition|)
block|{
name|Value
index|[]
name|values
init|=
name|getValues
argument_list|()
decl_stmt|;
if|if
condition|(
name|values
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// retrieve the type from the property definition
return|return
name|getRequiredType
argument_list|(
name|PropertyType
operator|.
name|UNDEFINED
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|values
index|[
literal|0
index|]
operator|.
name|getType
argument_list|()
return|;
block|}
block|}
else|else
block|{
return|return
name|getValue
argument_list|()
operator|.
name|getType
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMultiple
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|checkStatus
argument_list|()
expr_stmt|;
return|return
name|dlg
operator|.
name|isMultivalue
argument_list|()
return|;
block|}
comment|//------------------------------------------------------------< private>---
comment|/**      * Return the length of the specified JCR value object.      *      * @param value The value.      * @return The length of the given value.      * @throws RepositoryException If an error occurs.      */
specifier|private
specifier|static
name|long
name|getLength
parameter_list|(
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
block|{
if|if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
return|return
name|value
operator|.
name|getBinary
argument_list|()
operator|.
name|getSize
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|value
operator|.
name|getString
argument_list|()
operator|.
name|length
argument_list|()
return|;
block|}
block|}
comment|/**     *     * @param defaultType     * @return the required type for this property.     * @throws javax.jcr.RepositoryException     */
specifier|private
name|int
name|getRequiredType
parameter_list|(
name|int
name|defaultType
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// check type according to definition of this property
name|PropertyDefinition
name|def
init|=
name|getDefinition
argument_list|()
decl_stmt|;
name|int
name|reqType
init|=
operator|(
name|def
operator|==
literal|null
operator|)
condition|?
name|PropertyType
operator|.
name|UNDEFINED
else|:
name|getDefinition
argument_list|()
operator|.
name|getRequiredType
argument_list|()
decl_stmt|;
if|if
condition|(
name|reqType
operator|==
name|PropertyType
operator|.
name|UNDEFINED
condition|)
block|{
if|if
condition|(
name|defaultType
operator|==
name|PropertyType
operator|.
name|UNDEFINED
condition|)
block|{
name|reqType
operator|=
name|PropertyType
operator|.
name|STRING
expr_stmt|;
block|}
else|else
block|{
name|reqType
operator|=
name|defaultType
expr_stmt|;
block|}
block|}
return|return
name|reqType
return|;
block|}
comment|/**     *     * @param value     * @param requiredType     * @throws RepositoryException     */
specifier|private
name|void
name|setValue
parameter_list|(
name|Value
name|value
parameter_list|,
name|int
name|requiredType
parameter_list|)
throws|throws
name|RepositoryException
block|{
assert|assert
operator|(
name|requiredType
operator|!=
name|PropertyType
operator|.
name|UNDEFINED
operator|)
assert|;
comment|// TODO check again if definition validation should be respected here.
if|if
condition|(
name|isMultiple
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Attempt to set a single value to multi-valued property."
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|dlg
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Value
name|targetValue
init|=
name|ValueHelper
operator|.
name|convert
argument_list|(
name|value
argument_list|,
name|requiredType
argument_list|,
name|sessionDelegate
operator|.
name|getValueFactory
argument_list|()
argument_list|)
decl_stmt|;
name|dlg
operator|.
name|setValue
argument_list|(
name|ValueConverter
operator|.
name|toCoreValue
argument_list|(
name|targetValue
argument_list|,
name|sessionDelegate
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    *    * @param values    * @param requiredType    * @throws RepositoryException    */
specifier|private
name|void
name|setValues
parameter_list|(
name|Value
index|[]
name|values
parameter_list|,
name|int
name|requiredType
parameter_list|)
throws|throws
name|RepositoryException
block|{
assert|assert
operator|(
name|requiredType
operator|!=
name|PropertyType
operator|.
name|UNDEFINED
operator|)
assert|;
comment|// TODO check again if definition validation should be respected here.
if|if
condition|(
operator|!
name|isMultiple
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ValueFormatException
argument_list|(
literal|"Attempt to set multiple values to single valued property."
argument_list|)
throw|;
block|}
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|dlg
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Value
index|[]
name|targetValues
init|=
name|ValueHelper
operator|.
name|convert
argument_list|(
name|values
argument_list|,
name|requiredType
argument_list|,
name|sessionDelegate
operator|.
name|getValueFactory
argument_list|()
argument_list|)
decl_stmt|;
name|dlg
operator|.
name|setValues
argument_list|(
name|ValueConverter
operator|.
name|toCoreValues
argument_list|(
name|targetValues
argument_list|,
name|sessionDelegate
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

