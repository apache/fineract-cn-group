/*
 * Copyright 2017 The Mifos Initiative
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mifos.group.api.v1;

@SuppressWarnings("unused")
public interface EventConstants {

  String DESTINATION = "group-v1";
  String SELECTOR_NAME = "action";

  String INITIALIZE = "initialize";
  String SELECTOR_INITIALIZE = SELECTOR_NAME + " = '" + INITIALIZE + "'";

  String POST_GROUP_DEFINITION = "post-group-definition";
  String SELECTOR_POST_GROUP_DEFINITION = SELECTOR_NAME + " = '" + POST_GROUP_DEFINITION + "'";

  String POST_GROUP = "post-group";
  String SELECTOR_POST_GROUP = SELECTOR_NAME + " = '" + POST_GROUP + "'";
  String PUT_GROUP = "put-group";
  String SELECTOR_PUT_GROUP = SELECTOR_NAME + " = '" + PUT_GROUP + "'";
  String ACTIVATE_GROUP = "activate-group";
  String SELECTOR_ACTIVATE_GROUP = SELECTOR_NAME + " = '" + ACTIVATE_GROUP + "'";
  String CLOSE_GROUP = "close-group";
  String SELECTOR_CLOSE_GROUP = SELECTOR_NAME + " = '" + CLOSE_GROUP + "'";
  String REOPEN_GROUP = "reopen-group";
  String SELECTOR_REOPEN_GROUP = SELECTOR_NAME + " = '" + REOPEN_GROUP + "'";

  String POST_MEETING = "post-meeting";
  String SELECTOR_POST_MEETING = SELECTOR_NAME + " = '" + POST_MEETING + "'";
  String PUT_MEETING = "put-meeting";
  String SELECTOR_PUT_MEETING = SELECTOR_NAME + " = '" + PUT_MEETING + "'";
}
