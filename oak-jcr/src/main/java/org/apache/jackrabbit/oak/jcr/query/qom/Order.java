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
name|query
operator|.
name|qom
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|qom
operator|.
name|QueryObjectModelConstants
import|;
end_import

begin_comment
comment|/**  * Enumeration of the JCR 2.0 query order.  *  * @since Apache Jackrabbit 2.0  */
end_comment

begin_enum
specifier|public
enum|enum
name|Order
block|{
name|ASCENDING
parameter_list|(
name|QueryObjectModelConstants
operator|.
name|JCR_ORDER_ASCENDING
parameter_list|)
operator|,
constructor|DESCENDING(QueryObjectModelConstants.JCR_ORDER_DESCENDING
block|)
enum|;
end_enum

begin_comment
comment|/**      * JCR name of this order.      */
end_comment

begin_decl_stmt
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|Order
argument_list|(
name|String
name|name
argument_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
block|;     }
comment|/**      * @return the JCR name of this order.      */
specifier|public
name|String
name|getName
argument_list|()
block|{
return|return
name|name
return|;
block|}
end_expr_stmt

begin_comment
comment|/**      * Return the order with the given JCR name.      *      * @param name the JCR name of an order.      * @return the order with the given name.      * @throws IllegalArgumentException if {@code name} is not a known JCR      *                                  order name.      */
end_comment

begin_function
specifier|public
specifier|static
name|Order
name|getOrderByName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Order
name|order
range|:
name|Order
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|order
operator|.
name|name
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|order
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown order name: "
operator|+
name|name
argument_list|)
throw|;
block|}
end_function

unit|}
end_unit

