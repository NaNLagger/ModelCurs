/**
 * Created by admin on 21.11.2016.
 */
class Entity {
    private var _state = false
    public var state: Boolean
        get() = _state
        set(value) {
            _state = value
        }
}