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
name|commons
operator|.
name|annotations
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
operator|.
name|ANNOTATION_TYPE
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
operator|.
name|CONSTRUCTOR
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
operator|.
name|METHOD
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
operator|.
name|PACKAGE
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|ElementType
operator|.
name|TYPE
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|RetentionPolicy
operator|.
name|CLASS
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Documented
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Retention
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Target
import|;
end_import

begin_comment
comment|/**  * Elements annotated @Internal are -- although possibly exported -- intended  * for Oak's internal use only. Such elements are not public by design and  * likely to be removed, have their signature change, or have their access level  * decreased in future versions without notice. @Internal elements are eligible  * for immediate modification or removal and are not subject to any policies  * with respect to deprecation.  *<p>  * Note that Oak APIs are considered internal use by default, unless the package  * they appear in is annotated with a {@link @Version} annotation with a value  * greater than "1.0.0".  */
end_comment

begin_annotation_defn
annotation|@
name|Documented
annotation|@
name|Retention
argument_list|(
name|CLASS
argument_list|)
annotation|@
name|Target
argument_list|(
block|{
name|TYPE
block|,
name|METHOD
block|,
name|CONSTRUCTOR
block|,
name|ANNOTATION_TYPE
block|,
name|PACKAGE
block|}
argument_list|)
specifier|public
annotation_defn|@interface
name|Internal
block|{
comment|/**      * @return (optional) reason for being internal      */
name|String
name|reason
parameter_list|()
default|default
literal|""
function_decl|;
comment|/**      * @return (optional) first package version making this API internal      */
name|String
name|since
parameter_list|()
default|default
literal|""
function_decl|;
block|}
end_annotation_defn

end_unit

