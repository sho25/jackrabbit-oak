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
name|nodetype
operator|.
name|constraint
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
name|javax
operator|.
name|jcr
operator|.
name|Value
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
name|Predicate
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
specifier|final
class|class
name|Constraints
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
name|Constraints
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Constraints
parameter_list|()
block|{     }
specifier|public
specifier|static
name|Predicate
argument_list|<
name|Value
argument_list|>
name|valueConstraint
parameter_list|(
name|int
name|type
parameter_list|,
name|String
name|constraint
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|PropertyType
operator|.
name|STRING
case|:
return|return
operator|new
name|StringConstraint
argument_list|(
name|constraint
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BINARY
case|:
return|return
operator|new
name|BinaryConstraint
argument_list|(
name|constraint
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|LONG
case|:
return|return
operator|new
name|LongConstraint
argument_list|(
name|constraint
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DOUBLE
case|:
return|return
operator|new
name|DoubleConstraint
argument_list|(
name|constraint
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DATE
case|:
return|return
operator|new
name|DateConstraint
argument_list|(
name|constraint
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|BOOLEAN
case|:
return|return
operator|new
name|BooleanConstraint
argument_list|(
name|constraint
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|NAME
case|:
return|return
operator|new
name|NameConstraint
argument_list|(
name|constraint
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|PATH
case|:
return|return
operator|new
name|PathConstraint
argument_list|(
name|constraint
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|REFERENCE
case|:
return|return
operator|new
name|ReferenceConstraint
argument_list|(
name|constraint
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|WEAKREFERENCE
case|:
return|return
operator|new
name|ReferenceConstraint
argument_list|(
name|constraint
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|URI
case|:
return|return
operator|new
name|StringConstraint
argument_list|(
name|constraint
argument_list|)
return|;
case|case
name|PropertyType
operator|.
name|DECIMAL
case|:
return|return
operator|new
name|DecimalConstraint
argument_list|(
name|constraint
argument_list|)
return|;
default|default:
name|String
name|msg
init|=
literal|"Invalid property type: "
operator|+
name|type
decl_stmt|;
name|log
operator|.
name|warn
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

