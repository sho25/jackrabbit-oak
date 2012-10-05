begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|query
operator|.
name|ast
package|;
end_package

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
name|commons
operator|.
name|PathUtils
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
name|PropertyStates
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
name|query
operator|.
name|index
operator|.
name|FilterImpl
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
name|ISO9075
import|;
end_import

begin_comment
comment|/**  * The function "name(..)".  */
end_comment

begin_class
specifier|public
class|class
name|NodeNameImpl
extends|extends
name|DynamicOperandImpl
block|{
specifier|private
specifier|final
name|String
name|selectorName
decl_stmt|;
specifier|private
name|SelectorImpl
name|selector
decl_stmt|;
specifier|public
name|NodeNameImpl
parameter_list|(
name|String
name|selectorName
parameter_list|)
block|{
name|this
operator|.
name|selectorName
operator|=
name|selectorName
expr_stmt|;
block|}
annotation|@
name|Override
name|boolean
name|accept
parameter_list|(
name|AstVisitor
name|v
parameter_list|)
block|{
return|return
name|v
operator|.
name|visit
argument_list|(
name|this
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
literal|"name("
operator|+
name|quote
argument_list|(
name|selectorName
argument_list|)
operator|+
literal|')'
return|;
block|}
specifier|public
name|void
name|bindSelector
parameter_list|(
name|SourceImpl
name|source
parameter_list|)
block|{
name|selector
operator|=
name|source
operator|.
name|getExistingSelector
argument_list|(
name|selectorName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|supportsRangeConditions
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyState
name|currentProperty
parameter_list|()
block|{
name|String
name|path
init|=
name|selector
operator|.
name|currentPath
argument_list|()
decl_stmt|;
comment|// Name escaping (convert space to _x0020_)
name|String
name|name
init|=
name|ISO9075
operator|.
name|encode
argument_list|(
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
argument_list|)
decl_stmt|;
name|CoreValue
name|v2
init|=
name|query
operator|.
name|getValueFactory
argument_list|()
operator|.
name|createValue
argument_list|(
name|name
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
decl_stmt|;
return|return
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"NAME"
argument_list|,
name|v2
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restrict
parameter_list|(
name|FilterImpl
name|f
parameter_list|,
name|Operator
name|operator
parameter_list|,
name|CoreValue
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|isName
argument_list|(
name|v
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid name value: "
operator|+
name|v
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|String
name|path
init|=
name|v
operator|.
name|getString
argument_list|()
decl_stmt|;
comment|// Name escaping (convert _x0020_ to space)
name|path
operator|=
name|decodeName
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|PathUtils
operator|.
name|isAbsolute
argument_list|(
name|path
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"NAME() comparison with absolute path are not allowed: "
operator|+
name|path
argument_list|)
throw|;
block|}
if|if
condition|(
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|path
argument_list|)
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"NAME() comparison with relative path are not allowed: "
operator|+
name|path
argument_list|)
throw|;
block|}
comment|// TODO support NAME(..) index conditions
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canRestrictSelector
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
return|return
name|s
operator|==
name|selector
return|;
block|}
specifier|private
name|String
name|decodeName
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|// Name escaping (convert _x0020_ to space)
name|path
operator|=
name|ISO9075
operator|.
name|decode
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|// normalize paths (./name> name)
name|path
operator|=
name|query
operator|.
name|getOakPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
comment|/**      * Validate that the given value can be converted to a JCR name.      *      * @param v the value      * @return true if it can be converted      */
specifier|private
specifier|static
name|boolean
name|isName
parameter_list|(
name|CoreValue
name|v
parameter_list|)
block|{
comment|// TODO correctly validate JCR names - see JCR 2.0 spec 3.2.4 Naming Restrictions
switch|switch
condition|(
name|v
operator|.
name|getType
argument_list|()
condition|)
block|{
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
name|BOOLEAN
case|:
return|return
literal|false
return|;
block|}
name|String
name|n
init|=
name|v
operator|.
name|getString
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|.
name|startsWith
argument_list|(
literal|"["
argument_list|)
operator|&&
operator|!
name|n
operator|.
name|endsWith
argument_list|(
literal|"]"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

