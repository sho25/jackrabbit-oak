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
name|spi
operator|.
name|query
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|CoreValue
import|;
end_import

begin_comment
comment|/**  * The filter for an index lookup.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Filter
block|{
comment|/**      * Get the list of property restrictions, if any.      *      * @return the conditions (an empty collection if not used)      */
name|Collection
argument_list|<
name|PropertyRestriction
argument_list|>
name|getPropertyRestrictions
parameter_list|()
function_decl|;
comment|/**      * Get the fulltext search conditions, if any.      *      * @return the conditions (an empty collection if not used)      */
name|Collection
argument_list|<
name|String
argument_list|>
name|getFulltextConditions
parameter_list|()
function_decl|;
comment|/**      * Get the property restriction for the given property, if any.      *      * @param propertyName the property name      * @return the restriction, or null if there is no restriction for this property      */
name|PropertyRestriction
name|getPropertyRestriction
parameter_list|(
name|String
name|propertyName
parameter_list|)
function_decl|;
comment|/**      * Get the path restriction type.      *      * @return the path restriction type      */
name|PathRestriction
name|getPathRestriction
parameter_list|()
function_decl|;
comment|/**      * Get the path, or "/" if there is no path restriction set.      *      * @return the path      */
name|String
name|getPath
parameter_list|()
function_decl|;
name|String
name|getNodeType
parameter_list|()
function_decl|;
comment|/**      * A restriction for a property.      */
class|class
name|PropertyRestriction
block|{
comment|/**          * The name of the property.          */
specifier|public
name|String
name|propertyName
decl_stmt|;
comment|/**          * The first value to read, or null to read from the beginning.          */
specifier|public
name|CoreValue
name|first
decl_stmt|;
comment|/**          * Whether values that match the first should be returned.          */
specifier|public
name|boolean
name|firstIncluding
decl_stmt|;
comment|/**          * The last value to read, or null to read until the end.          */
specifier|public
name|CoreValue
name|last
decl_stmt|;
comment|/**          * Whether values that match the last should be returned.          */
specifier|public
name|boolean
name|lastIncluding
decl_stmt|;
comment|/**          * Whether this is a like constraint. in this case only the 'first'          * value should be taken into consideration          */
specifier|public
name|boolean
name|isLike
decl_stmt|;
comment|/**          * The property type, if restricted.          * If not restricted, this field is set to PropertyType.UNDEFINED.          */
specifier|public
name|int
name|propertyType
init|=
name|PropertyType
operator|.
name|UNDEFINED
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|(
name|first
operator|==
literal|null
condition|?
literal|""
else|:
operator|(
operator|(
name|firstIncluding
condition|?
literal|"["
else|:
literal|"("
operator|)
operator|+
name|first
operator|)
operator|)
operator|+
literal|".."
operator|+
operator|(
name|last
operator|==
literal|null
condition|?
literal|""
else|:
name|last
operator|+
operator|(
name|lastIncluding
condition|?
literal|"]"
else|:
literal|")"
operator|)
operator|)
return|;
block|}
block|}
comment|/**      * The path restriction type.      */
enum|enum
name|PathRestriction
block|{
comment|/**          * A parent of this node          */
name|PARENT
argument_list|(
literal|"/.."
argument_list|)
block|,
comment|/**          * This exact node only.          */
name|EXACT
argument_list|(
literal|""
argument_list|)
block|,
comment|/**          * All direct child nodes.          */
name|DIRECT_CHILDREN
argument_list|(
literal|"/*"
argument_list|)
block|,
comment|/**          * All direct and indirect child nodes.          */
name|ALL_CHILDREN
argument_list|(
literal|"//*"
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
name|PathRestriction
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
block|}
end_interface

end_unit

