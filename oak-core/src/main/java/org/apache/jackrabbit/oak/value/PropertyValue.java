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
name|value
package|;
end_package

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
name|Iterator
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
name|Type
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

begin_class
specifier|public
class|class
name|PropertyValue
implements|implements
name|Comparable
argument_list|<
name|PropertyValue
argument_list|>
block|{
specifier|private
specifier|final
name|PropertyState
name|ps
decl_stmt|;
specifier|protected
name|PropertyValue
parameter_list|(
name|PropertyState
name|ps
parameter_list|)
block|{
name|this
operator|.
name|ps
operator|=
name|ps
expr_stmt|;
block|}
specifier|public
name|boolean
name|isArray
parameter_list|()
block|{
return|return
name|ps
operator|.
name|isArray
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Type
argument_list|<
name|?
argument_list|>
name|getType
parameter_list|()
block|{
return|return
name|ps
operator|.
name|getType
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getValue
parameter_list|(
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
return|return
name|ps
operator|.
name|getValue
argument_list|(
name|type
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getValue
parameter_list|(
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|,
name|int
name|index
parameter_list|)
block|{
return|return
name|ps
operator|.
name|getValue
argument_list|(
name|type
argument_list|,
name|index
argument_list|)
return|;
block|}
specifier|public
name|long
name|size
parameter_list|()
block|{
return|return
name|ps
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|long
name|size
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|ps
operator|.
name|size
argument_list|(
name|index
argument_list|)
return|;
block|}
specifier|public
name|int
name|count
parameter_list|()
block|{
return|return
name|ps
operator|.
name|count
argument_list|()
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|PropertyState
name|unwrap
parameter_list|()
block|{
return|return
name|ps
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|PropertyValue
name|p2
parameter_list|)
block|{
if|if
condition|(
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|!=
name|p2
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
condition|)
block|{
return|return
name|Integer
operator|.
name|signum
argument_list|(
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
operator|-
name|p2
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
argument_list|)
return|;
block|}
switch|switch
condition|(
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|BINARY
case|:
return|return
name|compare
argument_list|(
name|getValue
argument_list|(
name|Type
operator|.
name|BINARIES
argument_list|)
argument_list|,
name|p2
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARIES
argument_list|)
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
name|compare
argument_list|(
name|getValue
argument_list|(
name|Type
operator|.
name|DOUBLES
argument_list|)
argument_list|,
name|p2
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DOUBLES
argument_list|)
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DATE
case|:
return|return
name|compareAsDate
argument_list|(
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|,
name|p2
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
return|;
default|default:
return|return
name|compare
argument_list|(
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|,
name|p2
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|T
argument_list|>
parameter_list|>
name|int
name|compare
parameter_list|(
name|Iterable
argument_list|<
name|T
argument_list|>
name|p1
parameter_list|,
name|Iterable
argument_list|<
name|T
argument_list|>
name|p2
parameter_list|)
block|{
name|Iterator
argument_list|<
name|T
argument_list|>
name|i1
init|=
name|p1
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|T
argument_list|>
name|i2
init|=
name|p2
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i1
operator|.
name|hasNext
argument_list|()
operator|||
name|i2
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|i1
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
operator|!
name|i2
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|compare
init|=
name|i1
operator|.
name|next
argument_list|()
operator|.
name|compareTo
argument_list|(
name|i2
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|compare
operator|!=
literal|0
condition|)
block|{
return|return
name|compare
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
specifier|private
specifier|static
name|int
name|compareAsDate
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|p1
parameter_list|,
name|Iterable
argument_list|<
name|String
argument_list|>
name|p2
parameter_list|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|i1
init|=
name|p1
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|i2
init|=
name|p2
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i1
operator|.
name|hasNext
argument_list|()
operator|||
name|i2
operator|.
name|hasNext
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|i1
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
operator|!
name|i2
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|String
name|v1
init|=
name|i1
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|v2
init|=
name|i2
operator|.
name|next
argument_list|()
decl_stmt|;
name|Calendar
name|c1
init|=
name|ISO8601
operator|.
name|parse
argument_list|(
name|v1
argument_list|)
decl_stmt|;
name|Calendar
name|c2
init|=
name|ISO8601
operator|.
name|parse
argument_list|(
name|v2
argument_list|)
decl_stmt|;
name|int
name|compare
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|c1
operator|!=
literal|null
operator|&&
name|c2
operator|!=
literal|null
condition|)
block|{
name|compare
operator|=
name|c1
operator|.
name|compareTo
argument_list|(
name|c2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|compare
operator|=
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|compare
operator|!=
literal|0
condition|)
block|{
return|return
name|compare
return|;
block|}
block|}
return|return
literal|0
return|;
block|}
comment|// --------------------------------------------------------------< Object>
specifier|private
name|String
name|getInternalString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|String
argument_list|>
name|iterator
init|=
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
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
operator|.
name|tag
argument_list|()
operator|^
name|getInternalString
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
name|PropertyValue
condition|)
block|{
return|return
name|compareTo
argument_list|(
operator|(
name|PropertyValue
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
name|getInternalString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

