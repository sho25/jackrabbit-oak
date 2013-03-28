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
name|core
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
name|api
operator|.
name|TreeLocation
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

begin_comment
comment|/**  * AbstractPropertyLocation... TODO  */
end_comment

begin_class
specifier|abstract
class|class
name|AbstractPropertyLocation
parameter_list|<
name|T
extends|extends
name|Tree
parameter_list|>
extends|extends
name|AbstractTreeLocation
block|{
specifier|protected
specifier|final
name|AbstractNodeLocation
argument_list|<
name|T
argument_list|>
name|parentLocation
decl_stmt|;
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
name|AbstractPropertyLocation
parameter_list|(
name|AbstractNodeLocation
argument_list|<
name|T
argument_list|>
name|parentLocation
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|parentLocation
operator|=
name|checkNotNull
argument_list|(
name|parentLocation
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|checkNotNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|boolean
name|canRead
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|TreeLocation
name|getParent
parameter_list|()
block|{
return|return
name|parentLocation
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
name|parentLocation
operator|.
name|tree
operator|.
name|isConnected
argument_list|()
operator|&&
name|getProperty
argument_list|()
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyState
name|getProperty
parameter_list|()
block|{
name|PropertyState
name|property
init|=
name|parentLocation
operator|.
name|getPropertyState
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|canRead
argument_list|(
name|property
argument_list|)
condition|?
name|property
else|:
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
return|return
name|parentLocation
operator|.
name|tree
operator|.
name|getPropertyStatus
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentLocation
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|remove
parameter_list|()
block|{
name|parentLocation
operator|.
name|tree
operator|.
name|removeProperty
argument_list|(
name|name
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|set
parameter_list|(
name|PropertyState
name|property
parameter_list|)
block|{
name|parentLocation
operator|.
name|tree
operator|.
name|setProperty
argument_list|(
name|property
argument_list|)
expr_stmt|;
return|return
literal|true
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
name|add
argument_list|(
literal|"parent"
argument_list|,
name|parentLocation
argument_list|)
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|name
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

