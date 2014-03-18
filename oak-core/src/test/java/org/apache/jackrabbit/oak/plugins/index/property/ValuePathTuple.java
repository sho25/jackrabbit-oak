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
name|index
operator|.
name|property
package|;
end_package

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
name|Predicate
import|;
end_import

begin_comment
comment|/**  * convenience orderable object that represents a tuple of values and paths  *  * where the values are the indexed keys from the index and the paths are the path which hold the  * key  */
end_comment

begin_class
specifier|public
class|class
name|ValuePathTuple
implements|implements
name|Comparable
argument_list|<
name|ValuePathTuple
argument_list|>
block|{
specifier|private
name|String
name|value
decl_stmt|;
specifier|private
name|String
name|path
decl_stmt|;
comment|/**      * convenience Predicate for easing the testing      */
specifier|public
specifier|static
class|class
name|GreaterThanPredicate
implements|implements
name|Predicate
argument_list|<
name|ValuePathTuple
argument_list|>
block|{
comment|/**          * the value for comparison          */
specifier|private
name|String
name|value
decl_stmt|;
comment|/**          * whether we should include the value in the result          */
specifier|private
name|boolean
name|include
decl_stmt|;
specifier|public
name|GreaterThanPredicate
parameter_list|(
name|String
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
specifier|public
name|GreaterThanPredicate
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|include
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
name|include
operator|=
name|include
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|ValuePathTuple
name|arg0
parameter_list|)
block|{
return|return
operator|(
name|value
operator|.
name|compareTo
argument_list|(
name|arg0
operator|.
name|getValue
argument_list|()
argument_list|)
operator|<
literal|0
operator|)
operator|||
operator|(
name|include
operator|&&
name|value
operator|.
name|equals
argument_list|(
name|arg0
operator|.
name|getValue
argument_list|()
argument_list|)
operator|)
return|;
block|}
block|}
empty_stmt|;
specifier|public
specifier|static
class|class
name|LessThanPredicate
implements|implements
name|Predicate
argument_list|<
name|ValuePathTuple
argument_list|>
block|{
comment|/**          * the value for comparison          */
specifier|private
name|String
name|value
decl_stmt|;
comment|/**          * whether we should include the value in the result          */
specifier|private
name|boolean
name|include
decl_stmt|;
specifier|public
name|LessThanPredicate
parameter_list|(
name|String
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
specifier|public
name|LessThanPredicate
parameter_list|(
name|String
name|value
parameter_list|,
name|boolean
name|include
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
name|include
operator|=
name|include
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|ValuePathTuple
name|arg0
parameter_list|)
block|{
return|return
operator|(
name|value
operator|.
name|compareTo
argument_list|(
name|arg0
operator|.
name|getValue
argument_list|()
argument_list|)
operator|>
literal|0
operator|)
operator|||
operator|(
name|include
operator|&&
name|value
operator|.
name|equals
argument_list|(
name|arg0
operator|.
name|getValue
argument_list|()
argument_list|)
operator|)
return|;
block|}
block|}
name|ValuePathTuple
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|path
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
name|path
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|path
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|path
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|value
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|value
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
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
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ValuePathTuple
name|other
init|=
operator|(
name|ValuePathTuple
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|getPath
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|path
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
elseif|else
if|if
condition|(
operator|!
name|value
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getValue
argument_list|()
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
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|ValuePathTuple
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|this
operator|.
name|value
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|getValue
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|this
operator|.
name|value
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|getValue
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|this
operator|.
name|path
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|getPath
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|if
condition|(
name|this
operator|.
name|path
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|getPath
argument_list|()
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
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
name|String
operator|.
name|format
argument_list|(
literal|"value: %s - path: %s - hash: %s"
argument_list|,
name|value
argument_list|,
name|path
argument_list|,
name|super
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

