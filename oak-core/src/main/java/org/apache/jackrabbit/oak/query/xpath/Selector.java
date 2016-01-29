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
name|query
operator|.
name|xpath
package|;
end_package

begin_comment
comment|/**  * A selector.  */
end_comment

begin_class
class|class
name|Selector
block|{
comment|/**      * The selector name.      */
name|String
name|name
decl_stmt|;
comment|/**      * Whether this is the only selector in the query.      */
name|boolean
name|onlySelector
decl_stmt|;
comment|/**      * The node type, if set, or null.      */
name|String
name|nodeType
decl_stmt|;
comment|/**      * Whether this is a child node of the previous selector or a given path.      * Examples:      *<ul><li>/jcr:root/*      *</li><li>/jcr:root/test/*      *</li><li>/jcr:root/element()      *</li><li>/jcr:root/element(*)      *</li></ul>      */
name|boolean
name|isChild
decl_stmt|;
comment|/**      * Whether this is a parent node of the previous selector or given path.      * Examples:      *<ul><li>testroot//child/..[@foo1]      *</li><li>/jcr:root/test/descendant/..[@test]      *</li></ul>      */
name|boolean
name|isParent
decl_stmt|;
comment|/**      * Whether this is a descendant of the previous selector or a given path.      * Examples:      *<ul><li>/jcr:root//descendant      *</li><li>/jcr:root/test//descendant      *</li><li>/jcr:root[@x]      *</li><li>/jcr:root (just by itself)      *</li></ul>      */
name|boolean
name|isDescendant
decl_stmt|;
comment|/**      * The path (only used for the first selector).      */
name|String
name|path
init|=
literal|""
decl_stmt|;
comment|/**      * The node name, if set.      */
name|String
name|nodeName
decl_stmt|;
comment|/**      * The condition for this selector.      */
name|Expression
name|condition
decl_stmt|;
comment|/**      * The join condition from the previous selector.      */
name|Expression
name|joinCondition
decl_stmt|;
specifier|public
name|Selector
parameter_list|()
block|{     }
specifier|public
name|Selector
parameter_list|(
name|Selector
name|s
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|s
operator|.
name|name
expr_stmt|;
name|this
operator|.
name|onlySelector
operator|=
name|s
operator|.
name|onlySelector
expr_stmt|;
name|this
operator|.
name|nodeType
operator|=
name|s
operator|.
name|nodeType
expr_stmt|;
name|this
operator|.
name|isChild
operator|=
name|s
operator|.
name|isChild
expr_stmt|;
name|this
operator|.
name|isParent
operator|=
name|s
operator|.
name|isParent
expr_stmt|;
name|this
operator|.
name|isDescendant
operator|=
name|s
operator|.
name|isDescendant
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|s
operator|.
name|path
expr_stmt|;
name|this
operator|.
name|nodeName
operator|=
name|s
operator|.
name|nodeName
expr_stmt|;
name|this
operator|.
name|condition
operator|=
name|s
operator|.
name|condition
expr_stmt|;
name|this
operator|.
name|joinCondition
operator|=
name|s
operator|.
name|joinCondition
expr_stmt|;
block|}
block|}
end_class

end_unit

