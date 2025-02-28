import React from 'react'

const Select = (props) => {
    return(
        <div 
          id={props.divId} 
          className={props.divClassName + " input-field"}
          >
            <select
              id={props.name}
              name={props.name}
              value={props.value}
              onChange={props.handleChange}
              className="input-field"
              required = {props.required}
              >
              <option value="" disabled>{props.placeholder}</option>
              {props.options.map(option => {
                return (
                  <option
                    key={option}
                    value={option}
                    label={option}>{option}
                  </option>
                );
              })}
            </select>
      </div>)
}

export default Select;