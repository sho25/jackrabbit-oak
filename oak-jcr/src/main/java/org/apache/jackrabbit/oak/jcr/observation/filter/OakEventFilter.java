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
name|jcr
operator|.
name|observation
operator|.
name|filter
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
name|api
operator|.
name|observation
operator|.
name|JackrabbitEventFilter
import|;
end_import

begin_import
import|import
name|aQute
operator|.
name|bnd
operator|.
name|annotation
operator|.
name|ConsumerType
import|;
end_import

begin_comment
comment|/**  * Extension of the JackrabbitEventFilter that exposes Oak specific  * features.  *<p>  * Usage:  *<code>  * OakEventFilter oakFilter = FilterFactory.wrap(jackrabbitFilter);  * // then call extensions on OakEventFilters  * observationManager.addEventListener(listener, oakFilter);    *</code>  */
end_comment

begin_class
annotation|@
name|ConsumerType
specifier|public
specifier|abstract
class|class
name|OakEventFilter
extends|extends
name|JackrabbitEventFilter
block|{  }
end_class

end_unit

